package cozy.ai.reminder.service;

import cozy.ai.reminder.domain.ReminderList;
import cozy.ai.reminder.dto.ReminderListRequest;
import cozy.ai.reminder.dto.ReminderListResponse;
import cozy.ai.reminder.service.ports.in.ReminderListService;
import cozy.ai.reminder.repository.ReminderListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultReminderListService implements ReminderListService {

    private final ReminderListRepository reminderListRepository;

    @Override
    public List<ReminderListResponse> getAll() {
        return reminderListRepository.findAllByOrderByDisplayOrder().stream()
                .map(list -> ReminderListResponse.from(list, 0))
                .toList();
    }

    @Override
    public ReminderListResponse getById(Long id) {
        ReminderList list = findById(id);
        return ReminderListResponse.from(list, 0);
    }

    @Override
    @Transactional
    public ReminderListResponse create(ReminderListRequest request) {
        int nextOrder = reminderListRepository.countBy();

        ReminderList list = ReminderList.builder()
                .name(request.name())
                .color(request.color())
                .icon(request.icon())
                .displayOrder(nextOrder)
                .build();

        ReminderList saved = reminderListRepository.save(list);
        return ReminderListResponse.from(saved);
    }

    @Override
    @Transactional
    public ReminderListResponse update(Long id, ReminderListRequest request) {
        ReminderList list = findById(id);
        list.update(request.name(), request.color(), request.icon());
        return ReminderListResponse.from(list);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ReminderList list = findById(id);
        reminderListRepository.delete(list);
    }

    @Override
    @Transactional
    public void reorder(List<Long> ids) {
        for (int i = 0; i < ids.size(); i++) {
            ReminderList list = findById(ids.get(i));
            list.updateDisplayOrder(i);
        }
    }

    private ReminderList findById(Long id) {
        return reminderListRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ReminderList not found: " + id));
    }
}
