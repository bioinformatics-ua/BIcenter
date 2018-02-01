package repositories;

import com.google.inject.Inject;
import models.ComponentMetadata;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import java.util.List;

public class JPAComponentMetadataRepository extends JPARepository implements ComponentMetadataRepository {
    @Inject
    public JPAComponentMetadataRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    @Override
    public ComponentMetadata get(long id) {
        return wrap(em -> get(em, id));
    }

    @Override
    public ComponentMetadata add(ComponentMetadata componentMetadata) {
        return wrap(em -> createOrUpdate(em, componentMetadata));
    }

    @Override
    public List<ComponentMetadata> list() {
        return wrap(em -> list(em));
    }

    private List<ComponentMetadata> list(EntityManager em) {
        return em.createQuery("select p from ComponentMetadata p", ComponentMetadata.class).getResultList();
    }

    private ComponentMetadata get(EntityManager em, long id) {
        return em.find(ComponentMetadata.class, id);
    }

    private ComponentMetadata createOrUpdate(EntityManager em, ComponentMetadata componentMetadata) {
        em.merge(componentMetadata);
        return componentMetadata;
    }
}
