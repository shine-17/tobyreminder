package cozy.ai.reminder.dto;

public record ReminderRequest(
        String title,
        String memo,
        Long listId
) {
}
