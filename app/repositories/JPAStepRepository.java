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

    public static Step createOrUpdate(EntityManager em, Step step) {
        step = em.merge(step);
        return step;
    }

    public static boolean delete(EntityManager em, Step step){
        step =  em.find(Step.class, step.getId());
        em.remove(step);
        return true;
    }

    public static Step getByTaskAndGraphId(EntityManager em, long taskId, int graphId) {
        return em.createQuery("select p from Step p where taskSteps_id=:taskSteps_id and graphId=:idparam", Step.class)
                .setParameter("taskSteps_id",taskId)
                .setParameter("idparam",graphId)
                .getSingleResult();
    }

    public static List<Step> getByTask(EntityManager em, long taskId) {
        return em.createQuery("select p from Step p where taskSteps_id=:taskSteps_id", Step.class)
                .setParameter("taskSteps_id",taskId)
                .getResultList();
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
    public void delete(Step step) {
        wrap(em -> delete(em,step));
    }

    @Override
    public List<Step> list() {
        return wrap(em -> list(em));
    }

    @Override
    public Step getByTaskAndGraphId(long taskId, int graphId) { return wrap(em -> getByTaskAndGraphId(em,taskId,graphId)); }

    @Override
    public List<Step> getByTask(long taskId) { return wrap(em -> getByTask(em,taskId)); }
}