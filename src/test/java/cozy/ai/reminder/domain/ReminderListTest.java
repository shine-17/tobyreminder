package cozy.ai.reminder.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderListTest {

    @Nested
    @DisplayName("Builder 생성")
    class BuilderTest {

        @Test
        @DisplayName("모든 필드를 지정하여 생성한다")
        void createWithAllFields() {
            ReminderList list = ReminderList.builder()
                    .name("Personal")
                    .color("#007AFF")
                    .icon("list.bullet")
                    .displayOrder(0)
                    .isDefault(true)
                    .build();

            assertThat(list.getName()).isEqualTo("Personal");
            assertThat(list.getColor()).isEqualTo("#007AFF");
            assertThat(list.getIcon()).isEqualTo("list.bullet");
            assertThat(list.getDisplayOrder()).isEqualTo(0);
            assertThat(list.isDefault()).isTrue();
        }

        @Test
        @DisplayName("isDefault를 지정하지 않으면 false로 설정된다")
        void createWithDefaultIsDefault() {
            ReminderList list = ReminderList.builder()
                    .name("Work")
                    .color("#FF3B30")
                    .displayOrder(1)
                    .build();

            assertThat(list.isDefault()).isFalse();
        }

        @Test
        @DisplayName("icon은 nullable이다")
        void createWithoutIcon() {
            ReminderList list = ReminderList.builder()
                    .name("Shopping")
                    .color("#34C759")
                    .displayOrder(2)
                    .build();

            assertThat(list.getIcon()).isNull();
        }

        @Test
        @DisplayName("생성 시 createdAt과 updatedAt이 자동 설정된다")
        void createdAtAndUpdatedAtAreSet() {
            LocalDateTime before = LocalDateTime.now();

            ReminderList list = ReminderList.builder()
                    .name("Personal")
                    .color("#007AFF")
                    .displayOrder(0)
                    .build();

            LocalDateTime after = LocalDateTime.now();

            assertThat(list.getCreatedAt()).isNotNull();
            assertThat(list.getUpdatedAt()).isNotNull();
            assertThat(list.getCreatedAt()).isEqualTo(list.getUpdatedAt());
            assertThat(list.getCreatedAt()).isBetween(before, after);
        }
    }

    @Nested
    @DisplayName("update 메서드")
    class UpdateTest {

        @Test
        @DisplayName("name, color, icon을 모두 변경한다")
        void updateAllFields() {
            ReminderList list = createDefaultList();

            list.update("New Name", "#FF3B30", "star.fill");

            assertThat(list.getName()).isEqualTo("New Name");
            assertThat(list.getColor()).isEqualTo("#FF3B30");
            assertThat(list.getIcon()).isEqualTo("star.fill");
        }

        @Test
        @DisplayName("null인 필드는 변경하지 않는다")
        void updatePartialFields() {
            ReminderList list = ReminderList.builder()
                    .name("Personal")
                    .color("#007AFF")
                    .icon("list.bullet")
                    .displayOrder(0)
                    .build();

            list.update("Updated Name", null, null);

            assertThat(list.getName()).isEqualTo("Updated Name");
            assertThat(list.getColor()).isEqualTo("#007AFF");
            assertThat(list.getIcon()).isEqualTo("list.bullet");
        }

        @Test
        @DisplayName("update 시 updatedAt이 갱신된다")
        void updateRefreshesUpdatedAt() {
            ReminderList list = createDefaultList();
            LocalDateTime originalUpdatedAt = list.getUpdatedAt();

            list.update("Updated", null, null);

            assertThat(list.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
        }

        @Test
        @DisplayName("displayOrder를 변경한다")
        void updateDisplayOrder() {
            ReminderList list = createDefaultList();

            list.updateDisplayOrder(5);

            assertThat(list.getDisplayOrder()).isEqualTo(5);
        }

        @Test
        @DisplayName("displayOrder 변경 시 updatedAt이 갱신된다")
        void updateDisplayOrderRefreshesUpdatedAt() {
            ReminderList list = createDefaultList();
            LocalDateTime originalUpdatedAt = list.getUpdatedAt();

            list.updateDisplayOrder(3);

            assertThat(list.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
        }
    }

    private ReminderList createDefaultList() {
        return ReminderList.builder()
                .name("Personal")
                .color("#007AFF")
                .displayOrder(0)
                .build();
    }
}
