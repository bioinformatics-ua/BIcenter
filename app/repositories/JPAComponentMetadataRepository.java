package repositories;

import com.google.inject.Inject;
import models.ComponentMetadata;
import models.ComponentProperty;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import java.util.List;

public class JPAComponentMetadataRepository extends JPARepository implements ComponentMetadataRepository {
    @Inject
    public JPAComponentMetadataRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public static List<ComponentMetadata> list(EntityManager em) {
        return em.createQuery("select p from ComponentMetadata p", ComponentMetadata.class).getResultList();
    }

    public static ComponentMetadata get(EntityManager em, long id) {
        return em.find(ComponentMetadata.class, id);
    }

    public static ComponentMetadata createOrUpdate(EntityManager em, ComponentMetadata componentMetadata) {
        componentMetadata = em.merge(componentMetadata);
        return componentMetadata;
    }

    public ComponentMetadata getByComponentAndShortName(EntityManager em, long componentId, String shortName){
        return em.createQuery("select cm from ComponentMetadata cm where cm.componentProperty.component.id=:componentId and cm.shortName=:shortName", ComponentMetadata.class)
                .setParameter("componentId",componentId)
                .setParameter("shortName",shortName)
                .getSingleResult();
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

    @Override
    public ComponentMetadata getMetadataByComponentAndShortName(long componentId, String shortName) {
        return wrap(em -> getByComponentAndShortName(em, componentId, shortName));
    }
}
