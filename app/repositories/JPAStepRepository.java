package repositories;

import com.google.inject.Inject;
import models.Step;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import java.util.List;

public class JPAStepRepository extends JPARepository implements StepRepository {
    @Inject
    public JPAStepRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public static List<Step> list(EntityManager em) {
        return em.createQuery("select p from Step p", Step.class).getResultList();
    }

    public static Step get(EntityManager em, long id) {
        return em.find(Step.class, id);
    }

    public static Step createOrUpdate(EntityManager em, Step Step) {
        Step = em.merge(Step);
        return Step;
    }

    @Override
    public Step get(long id) {
        return wrap(em -> get(em, id));
    }

    @Override
    public Step add(Step Step) {
        return wrap(em -> createOrUpdate(em, Step));
    }

    @Override
    public List<Step> list() {
        return wrap(em -> list(em));
    }
}

