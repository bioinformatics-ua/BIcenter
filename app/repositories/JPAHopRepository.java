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

    public static Hop createOrUpdate(EntityManager em, Hop Hop) {
        Hop = em.merge(Hop);
        return Hop;
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
    public List<Hop> list() {
        return wrap(em -> list(em));
    }
}


