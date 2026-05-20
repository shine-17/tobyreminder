package cozy.ai.reminder.service.ports.in;

import cozy.ai.reminder.dto.request.ReminderListRequest;
import cozy.ai.reminder.dto.response.ReminderListResponse;

import java.util.List;

public interface ReminderListService {

    List<ReminderListResponse> getAll();

    ReminderListResponse getById(Long id);

    ReminderListResponse create(ReminderListRequest request);

    ReminderListResponse update(Long id, ReminderListRequest request);

    void delete(Long id);

    void reorder(List<Long> ids);
}
