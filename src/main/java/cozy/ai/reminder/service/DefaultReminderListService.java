package cozy.ai.reminder.service;

import cozy.ai.reminder.domain.ReminderList;
import cozy.ai.reminder.dto.request.ReminderListRequest;
import cozy.ai.reminder.dto.response.ReminderListResponse;
import cozy.ai.reminder.repository.ReminderListRepository;
import cozy.ai.reminder.repository.ReminderRepository;
import cozy.ai.reminder.service.ports.in.ReminderListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultReminderListService implements ReminderListService {

    private final ReminderListRepository reminderListRepository;
    private final ReminderRepository reminderRepository;

    @Override
    public List<ReminderListResponse> getAll() {
        Map<Long, Long> countMap = reminderRepository.countActivePerList();
        return reminderListRepository.findAllByOrderByDisplayOrder().stream()
                .map(list -> ReminderListResponse.from(list,
                        countMap.getOrDefault(list.getId(), 0L)))
                .toList();
    }

    @Override
    public ReminderListResponse getById(Long id) {
        ReminderList list = findById(id);
        long count = reminderRepository.countByListIdAndCompletedFalse(id);
        return ReminderListResponse.from(list, count);
    }

    @Override
    @Transactional
    public ReminderListResponse create(ReminderListRequest request) {
        int nextOrder = reminderListRepository.countBy();

        ReminderList list = ReminderList.builder()
                .name(HtmlSanitizer.sanitize(request.name()))
                .color(request.color())
                .icon(HtmlSanitizer.sanitize(request.icon()))
                .displayOrder(nextOrder)
                .build();

        ReminderList saved = reminderListRepository.save(list);
        return ReminderListResponse.from(saved);
    }

    @Override
    @Transactional
    public ReminderListResponse update(Long id, ReminderListRequest request) {
        ReminderList list = findById(id);
        list.update(HtmlSanitizer.sanitize(request.name()), request.color(), HtmlSanitizer.sanitize(request.icon()));
        return ReminderListResponse.from(list);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ReminderList list = findById(id);
        reminderListRepository.delete(list); // cascade handles reminders
    }

    @Override
    @Transactional
    public void reorder(List<Long> ids) {
        if (ids.isEmpty()) return;

        List<ReminderList> lists = reminderListRepository.findAllById(ids);
        Map<Long, ReminderList> listMap = lists.stream()
                .collect(java.util.stream.Collectors.toMap(ReminderList::getId, l -> l));

        for (int i = 0; i < ids.size(); i++) {
            ReminderList list = listMap.get(ids.get(i));
            if (list == null) {
                throw new NoSuchElementException("ReminderList not found: " + ids.get(i));
            }
            list.updateDisplayOrder(i);
        }
    }

    private ReminderList findById(Long id) {
        return reminderListRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ReminderList not found: " + id));
    }
}
