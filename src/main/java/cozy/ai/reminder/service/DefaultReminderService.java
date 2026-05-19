package cozy.ai.reminder.service;

import cozy.ai.reminder.domain.Reminder;
import cozy.ai.reminder.domain.ReminderList;
import cozy.ai.reminder.dto.ReminderRequest;
import cozy.ai.reminder.dto.ReminderResponse;
import cozy.ai.reminder.repository.ReminderListRepository;
import cozy.ai.reminder.repository.ReminderRepository;
import cozy.ai.reminder.service.ports.in.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultReminderService implements ReminderService {

    private final ReminderRepository reminderRepository;
    private final ReminderListRepository reminderListRepository;

    @Override
    @Transactional
    public ReminderResponse create(ReminderRequest request) {
        ReminderList list = reminderListRepository.findById(request.listId())
                .orElseThrow(() -> new NoSuchElementException("ReminderList not found: " + request.listId()));

        int nextOrder = reminderRepository.countByListIdAndCompletedFalse(list.getId());

        Reminder reminder = Reminder.builder()
                .title(HtmlSanitizer.sanitize(request.title()))
                .memo(HtmlSanitizer.sanitize(request.memo()))
                .displayOrder(nextOrder)
                .list(list)
                .build();

        Reminder saved = reminderRepository.save(reminder);
        return ReminderResponse.from(saved);
    }

    @Override
    public ReminderResponse getById(Long id) {
        Reminder reminder = findById(id);
        return ReminderResponse.from(reminder);
    }

    @Override
    public List<ReminderResponse> getByListId(Long listId, boolean includeCompleted) {
        List<Reminder> active = reminderRepository.findByListIdAndCompletedFalseOrderByDisplayOrder(listId);
        List<ReminderResponse> result = new ArrayList<>(active.stream()
                .map(ReminderResponse::from)
                .toList());

        if (includeCompleted) {
            List<Reminder> completed = reminderRepository.findByListIdAndCompletedTrueOrderByCompletedAtDesc(listId);
            result.addAll(completed.stream().map(ReminderResponse::from).toList());
        }

        return result;
    }

    @Override
    @Transactional
    public ReminderResponse update(Long id, ReminderRequest request) {
        Reminder reminder = findById(id);
        reminder.update(HtmlSanitizer.sanitize(request.title()), HtmlSanitizer.sanitize(request.memo()));
        return ReminderResponse.from(reminder);
    }

    @Override
    @Transactional
    public ReminderResponse toggleComplete(Long id) {
        Reminder reminder = findById(id);
        reminder.toggleComplete();
        return ReminderResponse.from(reminder);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Reminder reminder = findById(id);
        reminderRepository.delete(reminder);
    }

    private Reminder findById(Long id) {
        return reminderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reminder not found: " + id));
    }
}
