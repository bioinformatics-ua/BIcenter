package repositories;

import com.google.inject.Inject;
import models.ComponentCategory;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

public class JPAComponentCategoryRepository extends JPARepository implements ComponentCategoryRepository {
    @Inject
    public JPAComponentCategoryRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public static List<ComponentCategory> list(EntityManager em) {
        return em.createQuery("select p from ComponentCategory p", ComponentCategory.class).getResultList();
    }

    public static ComponentCategory getOrCreate(EntityManager em, String name) {
        try {
            return em
                    .createQuery("select p from ComponentCategory p where p.name=:name", ComponentCategory.class)
                    .setParameter("name", name)
                    .getSingleResult();
        }
        catch(NoResultException e){
            return createOrUpdate(em,new ComponentCategory(name));
        }
    }

    public static ComponentCategory createOrUpdate(EntityManager em, ComponentCategory componentCategory) {
        componentCategory = em.merge(componentCategory);
        return componentCategory;
    }

    @Override
    public ComponentCategory get(String name) {
        return wrap(em -> getOrCreate(em, name));
    }

    @Override
    public ComponentCategory add(ComponentCategory componentCategory) {
        return wrap(em -> createOrUpdate(em, componentCategory));
    }

    @Override
    public List<ComponentCategory> list() {
        return wrap(em -> list(em));
    }
}
