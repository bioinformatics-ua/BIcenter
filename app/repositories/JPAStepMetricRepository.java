package repositories;

import com.google.inject.Inject;
import models.StepMetric;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import java.util.List;

public class JPAStepMetricRepository extends JPARepository implements StepMetricRepository {
    @Inject
    public JPAStepMetricRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public static List<StepMetric> list(EntityManager em) {
        return em.createQuery("select p from StepMetric p", StepMetric.class).getResultList();
    }

    public static StepMetric get(EntityManager em, long id) {
        return em.find(StepMetric.class, id);
    }

    public static StepMetric createOrUpdate(EntityManager em, StepMetric stepMetric) {
        stepMetric = em.merge(stepMetric);
        return stepMetric;
    }

    public static StepMetric getByExecutionAndStepLabel(EntityManager em, long executionId, String stepLabel) {
        return em.createQuery("select p from StepMetric p where p.execution.id=:executionId and p.step.label=:stepLabel", StepMetric.class)
                .setParameter("executionId",executionId)
                .setParameter("stepLabel",stepLabel)
                .getSingleResult();
    }

    @Override
    public StepMetric get(long id) {
        return wrap(em -> get(em, id));
    }

    @Override
    public StepMetric add(StepMetric stepMetric) {
        return wrap(em -> createOrUpdate(em, stepMetric));
    }

    @Override
    public List<StepMetric> list() {
        return wrap(em -> list(em));
    }

    @Override
    public StepMetric getByExecutionAndStepLabel(long executionId, String stepLabel) {
        return wrap(em -> getByExecutionAndStepLabel(em, executionId, stepLabel));
    }
}
