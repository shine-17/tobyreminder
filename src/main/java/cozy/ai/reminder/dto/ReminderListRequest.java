package cozy.ai.reminder.dto;

import lombok.Builder;

@Builder
public record ReminderListRequest(
        String name,
        String color,
        String icon
) {
}
