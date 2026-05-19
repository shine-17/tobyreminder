package cozy.ai.reminder.service;

import cozy.ai.reminder.domain.Reminder;
import cozy.ai.reminder.domain.ReminderList;
import cozy.ai.reminder.dto.ReminderListRequest;
import cozy.ai.reminder.dto.ReminderListResponse;
import cozy.ai.reminder.service.ports.in.ReminderListService;
import cozy.ai.reminder.repository.ReminderListRepository;
import cozy.ai.reminder.repository.ReminderRepository;
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
class ReminderListServiceTest {

    @Autowired
    private ReminderListService reminderListService;

    @Autowired
    private ReminderListRepository reminderListRepository;

    @Autowired
    private ReminderRepository reminderRepository;

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAllInBatch();
        reminderListRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("getAll")
    class GetAllTest {

        @Test
        @DisplayName("전체 리스트를 displayOrder 순으로 반환한다")
        void returnsAllListsOrderedByDisplayOrder() {
            saveList("Work", "#FF3B30", 1);
            saveList("Personal", "#007AFF", 0);

            List<ReminderListResponse> result = reminderListService.getAll();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).name()).isEqualTo("Personal");
            assertThat(result.get(1).name()).isEqualTo("Work");
        }

        @Test
        @DisplayName("리스트가 없으면 빈 리스트를 반환한다")
        void returnsEmptyListWhenNoLists() {
            List<ReminderListResponse> result = reminderListService.getAll();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("각 리스트의 미완료 리마인더 수를 정확히 반환한다")
        void returnsCorrectReminderCountPerList() {
            ReminderList work = saveList("Work", "#FF3B30", 0);
            ReminderList personal = saveList("Personal", "#007AFF", 1);
            ReminderList empty = saveList("Empty", "#34C759", 2);

            // Work: 3 active, 1 completed
            saveReminder("Task 1", work, false);
            saveReminder("Task 2", work, false);
            saveReminder("Task 3", work, false);
            saveReminder("Done task", work, true);

            // Personal: 1 active
            saveReminder("Read book", personal, false);

            // Empty: 0 reminders

            List<ReminderListResponse> result = reminderListService.getAll();

            assertThat(result).hasSize(3);
            assertThat(result.get(0).reminderCount()).isEqualTo(3); // Work
            assertThat(result.get(1).reminderCount()).isEqualTo(1); // Personal
            assertThat(result.get(2).reminderCount()).isEqualTo(0); // Empty
        }
    }

    @Nested
    @DisplayName("getById")
    class GetByIdTest {

        @Test
        @DisplayName("ID로 리스트를 조회한다")
        void returnsListById() {
            ReminderList saved = saveList("Personal", "#007AFF", 0);

            ReminderListResponse result = reminderListService.getById(saved.getId());

            assertThat(result.name()).isEqualTo("Personal");
            assertThat(result.color()).isEqualTo("#007AFF");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 예외가 발생한다")
        void throwsWhenNotFound() {
            assertThatThrownBy(() -> reminderListService.getById(99L))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("create")
    class CreateTest {

        @Test
        @DisplayName("새 리스트를 생성하고 displayOrder를 자동 부여한다")
        void createsListWithAutoDisplayOrder() {
            saveList("Existing", "#007AFF", 0);

            ReminderListRequest request = new ReminderListRequest("Shopping", "#34C759", "cart");
            ReminderListResponse result = reminderListService.create(request);

            assertThat(result.id()).isNotNull();
            assertThat(result.name()).isEqualTo("Shopping");
            assertThat(result.color()).isEqualTo("#34C759");
            assertThat(result.icon()).isEqualTo("cart");
            assertThat(result.displayOrder()).isEqualTo(1);
        }

        @Test
        @DisplayName("첫 리스트 생성 시 displayOrder는 0이다")
        void firstListHasDisplayOrderZero() {
            ReminderListRequest request = new ReminderListRequest("First", "#007AFF", null);
            ReminderListResponse result = reminderListService.create(request);

            assertThat(result.displayOrder()).isEqualTo(0);
        }

        @Test
        @DisplayName("생성된 리스트가 DB에 저장된다")
        void persistsToDatabase() {
            ReminderListRequest request = new ReminderListRequest("Personal", "#007AFF", "list");
            ReminderListResponse result = reminderListService.create(request);

            ReminderList found = reminderListRepository.findById(result.id()).orElseThrow();
            assertThat(found.getName()).isEqualTo("Personal");
            assertThat(found.getCreatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTest {

        @Test
        @DisplayName("리스트의 이름, 색상, 아이콘을 수정한다")
        void updatesListFields() {
            ReminderList saved = saveList("Old", "#007AFF", 0);

            ReminderListRequest request = new ReminderListRequest("New", "#FF3B30", "star");
            ReminderListResponse result = reminderListService.update(saved.getId(), request);

            assertThat(result.name()).isEqualTo("New");
            assertThat(result.color()).isEqualTo("#FF3B30");
            assertThat(result.icon()).isEqualTo("star");
        }

        @Test
        @DisplayName("null 필드는 기존 값을 유지한다")
        void preservesFieldsWhenNull() {
            ReminderList saved = saveList("Personal", "#007AFF", 0);

            ReminderListRequest request = new ReminderListRequest("Updated", null, null);
            ReminderListResponse result = reminderListService.update(saved.getId(), request);

            assertThat(result.name()).isEqualTo("Updated");
            assertThat(result.color()).isEqualTo("#007AFF");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 수정하면 예외가 발생한다")
        void throwsWhenNotFound() {
            assertThatThrownBy(() -> reminderListService.update(99L,
                    new ReminderListRequest("name", "#000", null)))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTest {

        @Test
        @DisplayName("리스트를 삭제한다")
        void deletesList() {
            ReminderList saved = saveList("Personal", "#007AFF", 0);

            reminderListService.delete(saved.getId());

            assertThat(reminderListRepository.findById(saved.getId())).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID로 삭제하면 예외가 발생한다")
        void throwsWhenNotFound() {
            assertThatThrownBy(() -> reminderListService.delete(99L))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("reorder")
    class ReorderTest {

        @Test
        @DisplayName("ID 순서대로 displayOrder를 재배치한다")
        void reordersDisplayOrder() {
            ReminderList a = saveList("A", "#007AFF", 0);
            ReminderList b = saveList("B", "#FF3B30", 1);
            ReminderList c = saveList("C", "#34C759", 2);

            reminderListService.reorder(List.of(c.getId(), a.getId(), b.getId()));

            assertThat(reminderListRepository.findById(c.getId()).orElseThrow().getDisplayOrder()).isEqualTo(0);
            assertThat(reminderListRepository.findById(a.getId()).orElseThrow().getDisplayOrder()).isEqualTo(1);
            assertThat(reminderListRepository.findById(b.getId()).orElseThrow().getDisplayOrder()).isEqualTo(2);
        }

        @Test
        @DisplayName("빈 리스트로 reorder해도 예외가 발생하지 않는다")
        void reorderWithEmptyListDoesNotThrow() {
            reminderListService.reorder(List.of());

            // 기존 리스트에 영향 없음
            assertThat(reminderListRepository.findAll()).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID로 reorder하면 예외가 발생한다")
        void reorderWithInvalidIdThrows() {
            ReminderList a = saveList("A", "#007AFF", 0);

            assertThatThrownBy(() -> reminderListService.reorder(List.of(a.getId(), 99L)))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    private ReminderList saveList(String name, String color, int displayOrder) {
        return reminderListRepository.save(
                ReminderList.builder()
                        .name(name)
                        .color(color)
                        .displayOrder(displayOrder)
                        .build()
        );
    }

    private Reminder saveReminder(String title, ReminderList list, boolean completed) {
        Reminder reminder = Reminder.builder()
                .title(title)
                .displayOrder(0)
                .list(list)
                .build();
        if (completed) {
            reminder.toggleComplete();
        }
        return reminderRepository.save(reminder);
    }
}
