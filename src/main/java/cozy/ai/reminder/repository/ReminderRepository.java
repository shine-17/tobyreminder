package cozy.ai.reminder.repository;

import cozy.ai.reminder.domain.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByListIdAndCompletedFalseOrderByDisplayOrder(Long listId);

    List<Reminder> findByListIdAndCompletedTrueOrderByCompletedAtDesc(Long listId);

    int countByListIdAndCompletedFalse(Long listId);

    void deleteByListId(Long listId);
}
