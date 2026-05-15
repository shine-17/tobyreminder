package cozy.ai.reminder.repository;

import cozy.ai.reminder.domain.ReminderList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReminderListRepository extends JpaRepository<ReminderList, Long> {

    List<ReminderList> findAllByOrderByDisplayOrder();

    int countBy();
}
