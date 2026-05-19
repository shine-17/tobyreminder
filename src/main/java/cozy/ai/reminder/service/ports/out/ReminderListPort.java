package cozy.ai.reminder.service.ports.out;

import cozy.ai.reminder.domain.ReminderList;

import java.util.List;

/**
 * 리마인더 리스트 영속성 포트.
 * JpaRepository 기본 메서드(save, findById, delete, findAllById)는 제외.
 */
public interface ReminderListPort {

    List<ReminderList> findAllByOrderByDisplayOrder();

    int countBy();
}
