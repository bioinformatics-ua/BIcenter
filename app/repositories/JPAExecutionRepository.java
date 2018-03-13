package repositories;

import com.google.inject.Inject;
import models.Execution;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import java.util.List;

public class JPAExecutionRepository extends JPARepository implements ExecutionRepository {
    @Inject
    public JPAExecutionRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public static List<Execution> list(EntityManager em) {
        return em.createQuery("select p from Execution p", Execution.class).getResultList();
    }

    public static Execution get(EntityManager em, long id) {
        return em.find(Execution.class, id);
    }

    public static Execution createOrUpdate(EntityManager em, Execution execution) {
        execution = em.merge(execution);
        return execution;
    }

    @Override
    public Execution get(long id) {
        return wrap(em -> get(em, id));
    }

    @Override
    public Execution add(Execution execution) {
        return wrap(em -> createOrUpdate(em, execution));
    }

    @Override
    public List<Execution> list() {
        return wrap(em -> list(em));
    }
}
