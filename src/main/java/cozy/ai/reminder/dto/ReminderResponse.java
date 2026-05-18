package cozy.ai.reminder.dto;

import cozy.ai.reminder.domain.Reminder;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReminderResponse(
        Long id,
        String title,
        String memo,
        Boolean completed,
        LocalDateTime completedAt,
        Integer displayOrder,
        Long listId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReminderResponse from(Reminder reminder) {
        return ReminderResponse.builder()
                .id(reminder.getId())
                .title(reminder.getTitle())
                .memo(reminder.getMemo())
                .completed(reminder.getCompleted())
                .completedAt(reminder.getCompletedAt())
                .displayOrder(reminder.getDisplayOrder())
                .listId(reminder.getListId())
                .createdAt(reminder.getCreatedAt())
                .updatedAt(reminder.getUpdatedAt())
                .build();
    }
}
