package repositories;

import com.google.inject.Inject;
import models.Step;
import models.StepProperty;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
        stepProperty = em.merge(stepProperty);
        return stepProperty;
    }

    public static List<StepProperty> getByComponentProperty(EntityManager em, long componentPropertyId) {
        try{
            return em.createQuery("select p from StepProperty p where componentProperty_id=:param", StepProperty.class)
                    .setParameter("param",componentPropertyId)
                    .getResultList();
        }
        catch(NoResultException e){
            return null;
        }
    }

    public static StepProperty getByStepAndComponentProperty(EntityManager em, long stepId, long componentPropertyId) {
        try{
            return em.createQuery("select p from StepProperty p where step_id=:stepId and componentProperty_id=:componentPropertyId", StepProperty.class)
                    .setParameter("stepId",stepId)
                    .setParameter("componentPropertyId",componentPropertyId)
                    .getSingleResult();
        }
        catch(NoResultException e){
            return null;
        }
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
    public List<StepProperty> list() { return wrap(em -> list(em)); }

    @Override
    public List<StepProperty> getByComponentProperty(long componentPropertyId) {
        return wrap(em -> getByComponentProperty(em,componentPropertyId));
    }

    @Override
    public StepProperty getByStepAndComponentProperty(long stepId, long componentPropertyId) {
        return wrap(em -> getByStepAndComponentProperty(em,stepId,componentPropertyId));
    }
}

