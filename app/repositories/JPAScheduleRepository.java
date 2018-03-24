package repositories;

import com.google.inject.Inject;
import models.Schedule;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import java.util.List;

public class JPAScheduleRepository extends JPARepository implements ScheduleRepository{
    @Inject
    public JPAScheduleRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public static List<Schedule> list(EntityManager em) {
        return em.createQuery("select p from Schedule p", Schedule.class).getResultList();
    }

    public static Schedule get(EntityManager em, long id) { return em.find(Schedule.class, id); }

    public static Schedule createOrUpdate(EntityManager em, Schedule schedule) {
        schedule = em.merge(schedule);
        return schedule;
    }

    @Override
    public Schedule get(long id) {
        return wrap(em -> get(em, id));
    }

    @Override
    public Schedule add(Schedule schedule) {
        return wrap(em -> createOrUpdate(em, schedule));
    }

    @Override
    public List<Schedule> list() {
        return wrap(em -> list(em));
    }
}
