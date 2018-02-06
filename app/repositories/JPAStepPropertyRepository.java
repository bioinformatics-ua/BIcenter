package repositories;

import com.google.inject.Inject;
import models.StepProperty;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import java.util.List;

public class JPAStepPropertyRepository extends JPARepository implements StepPropertyRepository {
    @Inject
    public JPAStepPropertyRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public static List<StepProperty> list(EntityManager em) {
        return em.createQuery("select p from StepProperty p", StepProperty.class).getResultList();
    }

    public static StepProperty get(EntityManager em, long id) {
        return em.find(StepProperty.class, id);
    }

    public static StepProperty createOrUpdate(EntityManager em, StepProperty stepProperty) {
        em.persist(stepProperty);
        return stepProperty;
    }

    @Override
    public StepProperty get(long id) {
        return wrap(em -> get(em, id));
    }

    @Override
    public StepProperty add(StepProperty StepProperty) {
        return wrap(em -> createOrUpdate(em, StepProperty));
    }

    @Override
    public List<StepProperty> list() {
        return wrap(em -> list(em));
    }
}
