package cozy.ai.reminder.service.ports.out;

import cozy.ai.reminder.domain.Reminder;

import java.util.List;
import java.util.Map;

/**
 * 리마인더 영속성 포트.
 * JpaRepository 기본 메서드(save, findById, delete)는 제외.
 */
public interface ReminderPort {

    List<Reminder> findByListIdAndCompletedFalseOrderByDisplayOrder(Long listId);

    List<Reminder> findByListIdAndCompletedTrueOrderByCompletedAtDesc(Long listId);

    int countByListIdAndCompletedFalse(Long listId);

    Map<Long, Long> countActivePerList();
}
