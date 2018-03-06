package repositories;

import com.google.inject.Inject;
import models.Status;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

public class JPAStatusRepository extends JPARepository implements StatusRepository {
    @Inject
    public JPAStatusRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public static List<Status> list(EntityManager em) {
        return em.createQuery("select p from Status p", Status.class).getResultList();
    }

    public static Status get(EntityManager em, long id) { return em.find(Status.class, id); }

    public static Status createOrUpdate(EntityManager em, Status stepStatus) {
        stepStatus = em.merge(stepStatus);
        return stepStatus;
    }

    public static Status getByExecutionAndStepLabel(EntityManager em, long executionId, String stepLabel) {
        try{
            return em.createQuery("select p from Status p where p.execution.id=:executionId and p.step.label=:stepLabel", Status.class)
                    .setParameter("executionId",executionId)
                    .setParameter("stepLabel",stepLabel)
                    .getSingleResult();
        }
        catch(NoResultException e){
            return null;
        }
    }

    @Override
    public Status get(long id) {
        return wrap(em -> get(em, id));
    }

    @Override
    public Status add(Status stepStatus) {
        return wrap(em -> createOrUpdate(em, stepStatus));
    }

    @Override
    public List<Status> list() {
        return wrap(em -> list(em));
    }

    @Override
    public Status getByExecutionAndStepLabel(long executionId, String stepLabel) {
        return wrap(em -> getByExecutionAndStepLabel(em,executionId,stepLabel));
    }
}
