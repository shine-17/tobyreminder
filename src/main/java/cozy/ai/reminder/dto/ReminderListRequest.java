package cozy.ai.reminder.dto;

import jakarta.validation.constraints.NotBlank;

public record ReminderListRequest(
        @NotBlank(message = "Name must not be blank")
        String name,
        @NotBlank(message = "Color must not be blank")
        String color,
        String icon
) {
}
