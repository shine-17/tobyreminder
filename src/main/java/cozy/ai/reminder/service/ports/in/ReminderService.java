package cozy.ai.reminder.service.ports.in;

import cozy.ai.reminder.dto.ReminderRequest;
import cozy.ai.reminder.dto.ReminderResponse;

import java.util.List;

public interface ReminderService {

    ReminderResponse create(ReminderRequest request);

    ReminderResponse getById(Long id);

    List<ReminderResponse> getByListId(Long listId, boolean includeCompleted);

    ReminderResponse update(Long id, ReminderRequest request);

    ReminderResponse toggleComplete(Long id);

    void delete(Long id);
}
