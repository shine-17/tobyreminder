package cozy.ai.reminder.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderTest {

    @Nested
    @DisplayName("Builder 생성")
    class BuilderTest {

        @Test
        @DisplayName("필수 필드로 생성한다")
        void createWithRequiredFields() {
            ReminderList list = createDefaultList();

            Reminder reminder = Reminder.builder()
                    .title("Buy groceries")
                    .memo("Milk and eggs")
                    .displayOrder(0)
                    .list(list)
                    .build();

            assertThat(reminder.getTitle()).isEqualTo("Buy groceries");
            assertThat(reminder.getMemo()).isEqualTo("Milk and eggs");
            assertThat(reminder.getDisplayOrder()).isEqualTo(0);
            assertThat(reminder.getList()).isEqualTo(list);
            assertThat(reminder.getCompleted()).isFalse();
            assertThat(reminder.getCompletedAt()).isNull();
        }

        @Test
        @DisplayName("생성 시 createdAt과 updatedAt이 자동 설정된다")
        void timestampsAreSet() {
            LocalDateTime before = LocalDateTime.now();

            Reminder reminder = createDefaultReminder();

            assertThat(reminder.getCreatedAt()).isNotNull();
            assertThat(reminder.getUpdatedAt()).isNotNull();
            assertThat(reminder.getCreatedAt()).isAfterOrEqualTo(before);
        }
    }

    @Nested
    @DisplayName("update 메서드")
    class UpdateTest {

        @Test
        @DisplayName("title과 memo를 변경한다")
        void updatesFields() {
            Reminder reminder = createDefaultReminder();

            reminder.update("New title", "New memo");

            assertThat(reminder.getTitle()).isEqualTo("New title");
            assertThat(reminder.getMemo()).isEqualTo("New memo");
        }

        @Test
        @DisplayName("null 필드는 변경하지 않는다")
        void preservesNullFields() {
            Reminder reminder = createDefaultReminder();

            reminder.update(null, "Updated memo");

            assertThat(reminder.getTitle()).isEqualTo("Test reminder");
            assertThat(reminder.getMemo()).isEqualTo("Updated memo");
        }

        @Test
        @DisplayName("update 시 updatedAt이 갱신된다")
        void refreshesUpdatedAt() {
            Reminder reminder = createDefaultReminder();
            LocalDateTime original = reminder.getUpdatedAt();

            reminder.update("New", null);

            assertThat(reminder.getUpdatedAt()).isAfterOrEqualTo(original);
        }
    }

    @Nested
    @DisplayName("toggleComplete 메서드")
    class ToggleCompleteTest {

        @Test
        @DisplayName("미완료 → 완료로 전환하면 completedAt이 설정된다")
        void completeSetsCompletedAt() {
            Reminder reminder = createDefaultReminder();

            reminder.toggleComplete();

            assertThat(reminder.getCompleted()).isTrue();
            assertThat(reminder.getCompletedAt()).isNotNull();
        }

        @Test
        @DisplayName("완료 → 미완료로 전환하면 completedAt이 null이 된다")
        void uncompleteClearsCompletedAt() {
            Reminder reminder = createDefaultReminder();
            reminder.toggleComplete(); // 완료

            reminder.toggleComplete(); // 미완료

            assertThat(reminder.getCompleted()).isFalse();
            assertThat(reminder.getCompletedAt()).isNull();
        }

        @Test
        @DisplayName("토글 시 updatedAt이 갱신된다")
        void refreshesUpdatedAt() {
            Reminder reminder = createDefaultReminder();
            LocalDateTime original = reminder.getUpdatedAt();

            reminder.toggleComplete();

            assertThat(reminder.getUpdatedAt()).isAfterOrEqualTo(original);
        }
    }

    private Reminder createDefaultReminder() {
        return Reminder.builder()
                .title("Test reminder")
                .memo("Test memo")
                .displayOrder(0)
                .list(createDefaultList())
                .build();
    }

    private ReminderList createDefaultList() {
        return ReminderList.builder()
                .name("Personal")
                .color("#007AFF")
                .displayOrder(0)
                .build();
    }
}
