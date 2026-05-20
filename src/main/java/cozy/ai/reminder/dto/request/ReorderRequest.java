package cozy.ai.reminder.dto.request;

import java.util.List;

public record ReorderRequest(
        List<Long> ids
) {
}
