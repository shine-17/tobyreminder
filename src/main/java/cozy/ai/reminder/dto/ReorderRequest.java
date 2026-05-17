package cozy.ai.reminder.dto;

import java.util.List;

public record ReorderRequest(
        List<Long> ids
) {
}
