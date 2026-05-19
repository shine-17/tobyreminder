package cozy.ai.reminder.repository;

import cozy.ai.reminder.domain.Reminder;
import cozy.ai.reminder.service.ports.out.ReminderPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface ReminderRepository extends JpaRepository<Reminder, Long>, ReminderPort {

    /**
     * 리스트별 미완료 리마인더 수를 한 번의 쿼리로 조회한다.
     * N+1 쿼리 방지용.
     */
    @Query("SELECT r.list.id, COUNT(r) FROM Reminder r WHERE r.completed = false GROUP BY r.list.id")
    List<Object[]> countActiveGroupByListId();

    default Map<Long, Long> countActivePerList() {
        return countActiveGroupByListId().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }

    List<Reminder> findByListIdAndCompletedFalseOrderByDisplayOrder(Long listId);

    List<Reminder> findByListIdAndCompletedTrueOrderByCompletedAtDesc(Long listId);

    int countByListIdAndCompletedFalse(Long listId);

    void deleteByListId(Long listId);
}
