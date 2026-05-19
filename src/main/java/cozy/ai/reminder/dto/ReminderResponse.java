package cozy.ai.reminder.dto;

import cozy.ai.reminder.domain.Reminder;

import java.time.LocalDateTime;

public record ReminderResponse(
        Long id,
        String title,
        String memo,
        boolean completed,
        LocalDateTime completedAt,
        Integer displayOrder,
        Long listId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReminderResponse from(Reminder reminder) {
        return new ReminderResponse(
                reminder.getId(),
                reminder.getTitle(),
                reminder.getMemo(),
                reminder.isCompleted(),
                reminder.getCompletedAt(),
                reminder.getDisplayOrder(),
                reminder.getListId(),
                reminder.getCreatedAt(),
                reminder.getUpdatedAt()
        );
    }
}
