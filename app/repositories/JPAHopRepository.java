package repositories;

import com.google.inject.Inject;
import models.Hop;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import java.util.List;

public class JPAHopRepository extends JPARepository implements HopRepository {
    @Inject
    public JPAHopRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public static List<Hop> list(EntityManager em) {
        return em.createQuery("select p from Hop p", Hop.class).getResultList();
    }

    public static Hop get(EntityManager em, long id) {
        return em.find(Hop.class, id);
    }

    public static Hop createOrUpdate(EntityManager em, Hop hop) {
        hop = em.merge(hop);
        return hop;
    }

    public static boolean delete(EntityManager em, Hop hop){
        hop =  em.find(Hop.class, hop.getId());
        em.remove(hop);
        return true;
    }

    public static List<Hop> getByTask(EntityManager em, long taskId) {
        return em.createQuery("select p from Hop p where taskHops_id=:taskHops_id", Hop.class)
                .setParameter("taskHops_id",taskId)
                .getResultList();
    }

    public static List<Hop> getBySource(EntityManager em, long stepId) {
        return em.createQuery("select p from Hop p where source_id=:source_id", Hop.class)
                .setParameter("source_id",stepId)
                .getResultList();
    }

    @Override
    public Hop get(long id) {
        return wrap(em -> get(em, id));
    }

    @Override
    public Hop add(Hop Hop) {
        return wrap(em -> createOrUpdate(em, Hop));
    }

    @Override
    public void delete(Hop hop) { wrap(em -> delete(em,hop)); }

    @Override
    public List<Hop> list() {
        return wrap(em -> list(em));
    }

    @Override
    public List<Hop> getByTask(long taskId) { return wrap(em -> getByTask(em,taskId)); }

    @Override
    public List<Hop> getBySource(long stepId) { return wrap(em -> getBySource(em,stepId)); }
}


