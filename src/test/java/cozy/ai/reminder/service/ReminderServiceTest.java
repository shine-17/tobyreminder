package cozy.ai.reminder.service;

import cozy.ai.reminder.domain.Reminder;
import cozy.ai.reminder.domain.ReminderList;
import cozy.ai.reminder.dto.ReminderRequest;
import cozy.ai.reminder.dto.ReminderResponse;
import cozy.ai.reminder.repository.ReminderListRepository;
import cozy.ai.reminder.repository.ReminderRepository;
import cozy.ai.reminder.service.ports.in.ReminderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ReminderServiceTest {

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private ReminderListRepository reminderListRepository;

    private ReminderList defaultList;

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAllInBatch();
        reminderListRepository.deleteAllInBatch();
        defaultList = reminderListRepository.save(
                ReminderList.builder().name("Personal").color("#007AFF").displayOrder(0).build());
    }

    @Nested
    @DisplayName("create")
    class CreateTest {

        @Test
        @DisplayName("리마인더를 생성한다")
        void createsReminder() {
            ReminderRequest request = new ReminderRequest("Buy milk", "From store", defaultList.getId());

            ReminderResponse result = reminderService.create(request);

            assertThat(result.id()).isNotNull();
            assertThat(result.title()).isEqualTo("Buy milk");
            assertThat(result.memo()).isEqualTo("From store");
            assertThat(result.completed()).isFalse();
            assertThat(result.listId()).isEqualTo(defaultList.getId());
        }

        @Test
        @DisplayName("displayOrder를 자동 부여한다")
        void autoAssignsDisplayOrder() {
            reminderService.create(new ReminderRequest("First", null, defaultList.getId()));
            ReminderResponse second = reminderService.create(new ReminderRequest("Second", null, defaultList.getId()));

            assertThat(second.displayOrder()).isEqualTo(1);
        }

        @Test
        @DisplayName("존재하지 않는 리스트 ID면 예외가 발생한다")
        void throwsWhenListNotFound() {
            assertThatThrownBy(() -> reminderService.create(new ReminderRequest("Test", null, 99L)))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("getById")
    class GetByIdTest {

        @Test
        @DisplayName("ID로 리마인더를 조회한다")
        void returnsReminder() {
            ReminderResponse created = reminderService.create(new ReminderRequest("Test", null, defaultList.getId()));

            ReminderResponse result = reminderService.getById(created.id());

            assertThat(result.title()).isEqualTo("Test");
        }

        @Test
        @DisplayName("응답에 올바른 listId가 포함된다")
        void returnsCorrectListId() {
            ReminderResponse created = reminderService.create(new ReminderRequest("Test", null, defaultList.getId()));

            ReminderResponse result = reminderService.getById(created.id());

            assertThat(result.listId()).isEqualTo(defaultList.getId());
        }

        @Test
        @DisplayName("존재하지 않는 ID면 예외가 발생한다")
        void throwsWhenNotFound() {
            assertThatThrownBy(() -> reminderService.getById(99L))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("getByListId")
    class GetByListIdTest {

        @Test
        @DisplayName("리스트의 미완료 리마인더만 반환한다")
        void returnsActiveOnly() {
            ReminderResponse r1 = reminderService.create(new ReminderRequest("Active", null, defaultList.getId()));
            ReminderResponse r2 = reminderService.create(new ReminderRequest("Done", null, defaultList.getId()));
            reminderService.toggleComplete(r2.id());

            List<ReminderResponse> result = reminderService.getByListId(defaultList.getId(), false);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).title()).isEqualTo("Active");
        }

        @Test
        @DisplayName("includeCompleted=true이면 완료 항목도 포함한다")
        void returnsAllWithCompleted() {
            reminderService.create(new ReminderRequest("Active", null, defaultList.getId()));
            ReminderResponse r2 = reminderService.create(new ReminderRequest("Done", null, defaultList.getId()));
            reminderService.toggleComplete(r2.id());

            List<ReminderResponse> result = reminderService.getByListId(defaultList.getId(), true);

            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTest {

        @Test
        @DisplayName("리마인더를 수정한다")
        void updatesReminder() {
            ReminderResponse created = reminderService.create(new ReminderRequest("Old", "Old memo", defaultList.getId()));

            ReminderResponse result = reminderService.update(created.id(), new ReminderRequest("New", "New memo", null));

            assertThat(result.title()).isEqualTo("New");
            assertThat(result.memo()).isEqualTo("New memo");
        }
    }

    @Nested
    @DisplayName("toggleComplete")
    class ToggleCompleteTest {

        @Test
        @DisplayName("미완료 → 완료로 토글한다")
        void completesReminder() {
            ReminderResponse created = reminderService.create(new ReminderRequest("Test", null, defaultList.getId()));

            ReminderResponse result = reminderService.toggleComplete(created.id());

            assertThat(result.completed()).isTrue();
            assertThat(result.completedAt()).isNotNull();
        }

        @Test
        @DisplayName("완료 → 미완료로 토글한다")
        void uncompletesReminder() {
            ReminderResponse created = reminderService.create(new ReminderRequest("Test", null, defaultList.getId()));
            reminderService.toggleComplete(created.id());

            ReminderResponse result = reminderService.toggleComplete(created.id());

            assertThat(result.completed()).isFalse();
            assertThat(result.completedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTest {

        @Test
        @DisplayName("리마인더를 삭제한다")
        void deletesReminder() {
            ReminderResponse created = reminderService.create(new ReminderRequest("Test", null, defaultList.getId()));

            reminderService.delete(created.id());

            assertThat(reminderRepository.findById(created.id())).isEmpty();
        }
    }
}
