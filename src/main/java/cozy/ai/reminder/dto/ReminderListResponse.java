package cozy.ai.reminder.dto;

import cozy.ai.reminder.domain.ReminderList;

import java.time.LocalDateTime;

public record ReminderListResponse(
        Long id,
        String name,
        String color,
        String icon,
        Integer displayOrder,
        boolean isDefault,
        long reminderCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReminderListResponse from(ReminderList list, long reminderCount) {
        return new ReminderListResponse(
                list.getId(),
                list.getName(),
                list.getColor(),
                list.getIcon(),
                list.getDisplayOrder(),
                list.isDefault(),
                reminderCount,
                list.getCreatedAt(),
                list.getUpdatedAt()
        );
    }

    public static ReminderListResponse from(ReminderList list) {
        return from(list, 0);
    }
}
