package cozy.ai.reminder.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReminderRequest(
        @NotBlank(message = "Title must not be blank")
        String title,
        String memo,
        @NotNull(message = "List ID is required")
        Long listId
) {
}
