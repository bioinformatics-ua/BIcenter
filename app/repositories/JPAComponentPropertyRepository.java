package repositories;

import com.google.inject.Inject;
import models.ComponentProperty;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import java.util.List;

public class JPAComponentPropertyRepository extends JPARepository implements ComponentPropertyRepository {
    @Inject
    public JPAComponentPropertyRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    @Override
    public ComponentProperty get(long id) {
        return wrap(em -> get(em, id));
    }

    @Override
    public ComponentProperty add(ComponentProperty componentProperty) {
        return wrap(em -> createOrUpdate(em, componentProperty));
    }

    @Override
    public List<ComponentProperty> list() {
        return wrap(em -> list(em));
    }

    private List<ComponentProperty> list(EntityManager em) {
        return em.createQuery("select p from ComponentProperty p", ComponentProperty.class).getResultList();
    }

    private ComponentProperty get(EntityManager em, long id) {
        return em.find(ComponentProperty.class, id);
    }

    private ComponentProperty createOrUpdate(EntityManager em, ComponentProperty componentProperty) {
        em.merge(componentProperty);
        return componentProperty;
    }
}

