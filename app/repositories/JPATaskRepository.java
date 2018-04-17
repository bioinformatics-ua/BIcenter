package repositories;

import com.google.inject.Inject;
import models.Task;
import org.hibernate.Hibernate;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

public class JPATaskRepository extends JPARepository implements TaskRepository {
    @Inject
    public JPATaskRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public static boolean delete(EntityManager em, long id) {
        Task server = em.find(Task.class, id);
        em.remove(server);
        return true;
    }

    public static List<Task> list(EntityManager em) {
        return em.createQuery("select p from Task p", Task.class).getResultList();
    }

    public static Task getByName(EntityManager em, String name) {
        return em.createQuery("select p from Task p where name=:nameparam", Task.class)
                .setParameter("nameparam",name)
                .getSingleResult();
    }

    public static List<String> getOpenTabs(EntityManager em){
        return em.createQuery("select name from Task p where open=1", String.class)
                .getResultList();
    }

    public static Task get(EntityManager em, long id) {
        return em.find(Task.class, id);
    }

    public static Task createOrUpdate(EntityManager em, Task task) {
        task = em.merge(task);
        return task;
    }

    @Override
    public Task get(long id) {
        return wrap(em -> get(em, id));
    }

    @Override
    public Task add(Task Task) {
        return wrap(em -> createOrUpdate(em, Task));
    }

    @Override
    public void delete(long id) { wrap(em -> delete(em, id)); }

    @Override
    public List<Task> list() {
        return wrap(em -> list(em));
    }

    @Override
    public Task getByName(String name) { return wrap(em -> getByName(em,name)); }

    @Override
    public List<String> getOpenTabs() { return wrap(em -> getOpenTabs(em)); }
}
