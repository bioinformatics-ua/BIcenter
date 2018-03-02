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

    public static List<ComponentProperty> list(EntityManager em) {
        return em.createQuery("select p from ComponentProperty p", ComponentProperty.class).getResultList();
    }

    public ComponentProperty getByComponentAndShortName(EntityManager em, long componentId, String shortName){
        return em.createQuery("select cp from ComponentProperty cp where cp.component.id=:componentId and cp.shortName=:shortName", ComponentProperty.class)
                .setParameter("componentId",componentId)
                .setParameter("shortName",shortName)
                .getSingleResult();
    }

    public static ComponentProperty get(EntityManager em, long id) {
        return em.find(ComponentProperty.class, id);
    }

    public static ComponentProperty createOrUpdate(EntityManager em, ComponentProperty componentProperty) {
        componentProperty = em.merge(componentProperty);
        return componentProperty;
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

    @Override
    public ComponentProperty getByComponentAndShortName(long componentId, String shortName) {
        return wrap(em -> getByComponentAndShortName(em,componentId,shortName));
    }
}

