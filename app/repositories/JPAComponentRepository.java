package repositories;

import com.google.inject.Inject;
import models.Component;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class JPAComponentRepository extends JPARepository implements ComponentRepository {
    @Inject
    public JPAComponentRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    @Override
    public Component get(long id) {
        return wrap(em -> get(em, id));
    }

    @Override
    public Component add(Component component) {
        return wrap(em -> createOrUpdate(em, component));
    }

    @Override
    public List<Component> list() {
        return wrap(em -> list(em));
    }

    private List<Component> list(EntityManager em) {
        return em.createQuery("select p from Component p", Component.class).getResultList();
    }

    private Component get(EntityManager em, long id) {
        return em.find(Component.class, id);
    }

    private Component createOrUpdate(EntityManager em, Component component) {
        em.merge(component);
        return component;
    }
}
