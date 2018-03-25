package repositories;

import com.google.inject.ImplementedBy;
import models.Schedule;

import java.util.List;

@ImplementedBy(JPAScheduleRepository.class)
public interface ScheduleRepository {
    Schedule get(long id);

    Schedule add(Schedule schedule);

    List<Schedule> list();

    void delete(Schedule schedule);
}
