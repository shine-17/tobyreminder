package cozy.ai.reminder.repository;

import cozy.ai.reminder.domain.ReminderList;
import cozy.ai.reminder.service.ports.out.ReminderListPort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReminderListRepository extends JpaRepository<ReminderList, Long>, ReminderListPort {

    List<ReminderList> findAllByOrderByDisplayOrder();

    int countBy();
}
