package repositories;

import com.google.inject.Inject;
import models.Component;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import java.util.List;

public class JPAComponentRepository extends JPARepository implements ComponentRepository {
    @Inject
    public JPAComponentRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public static List<Component> list(EntityManager em) {
        return em.createQuery("select p from Component p", Component.class).getResultList();
    }

    public static Component getByName(EntityManager em, String name) {
        return em.createQuery("select p from Component p where name=:nameparam", Component.class)
                .setParameter("nameparam",name)
                .getSingleResult();
    }

    public static Component get(EntityManager em, long id) {
        return em.find(Component.class, id);
    }

    public static Component createOrUpdate(EntityManager em, Component component) {
        em.persist(component);
        return component;
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

    @Override
    public Component getByName(String name) { return wrap(em -> getByName(em,name)); }
}
