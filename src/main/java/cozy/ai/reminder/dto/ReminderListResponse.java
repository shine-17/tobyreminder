package cozy.ai.reminder.dto;

import cozy.ai.reminder.domain.ReminderList;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReminderListResponse(
        Long id,
        String name,
        String color,
        String icon,
        Integer displayOrder,
        Boolean isDefault,
        long reminderCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReminderListResponse from(ReminderList list, long reminderCount) {
        return ReminderListResponse.builder()
                .id(list.getId())
                .name(list.getName())
                .color(list.getColor())
                .icon(list.getIcon())
                .displayOrder(list.getDisplayOrder())
                .isDefault(list.getIsDefault())
                .reminderCount(reminderCount)
                .createdAt(list.getCreatedAt())
                .updatedAt(list.getUpdatedAt())
                .build();
    }

    public static ReminderListResponse from(ReminderList list) {
        return from(list, 0);
    }
}
