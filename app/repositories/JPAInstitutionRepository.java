package repositories;

import com.google.inject.Inject;
import models.Hop;
import models.Institution;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import java.util.List;

public class JPAInstitutionRepository extends JPARepository implements InstitutionRepository {
    @Inject
    public JPAInstitutionRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public static List<Institution> list(EntityManager em) {
        return em.createQuery("select p from Institution p", Institution.class).getResultList();
    }

    public static Institution get(EntityManager em, long id) {
        return em.find(Institution.class, id);
    }

    public static Institution createOrUpdate(EntityManager em, Institution institution) {
        institution = em.merge(institution);
        return institution;
    }

    public static boolean delete(EntityManager em, Institution institution){
        institution =  em.find(Institution.class, institution.getId());
        em.remove(institution);
        return true;
    }

    @Override
    public Institution get(long id) {
        return wrap(em -> get(em, id));
    }

    @Override
    public Institution add(Institution institution) {
        return wrap(em -> createOrUpdate(em, institution));
    }

    @Override
    public void delete(Institution institution) { wrap(em -> delete(em,institution)); }

    @Override
    public List<Institution> list() {
        return wrap(em -> list(em));
    }
}
