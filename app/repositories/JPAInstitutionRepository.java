package repositories;

import com.google.inject.Inject;
import models.Institution;
import models.rbac.User;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

public class JPAInstitutionRepository extends JPARepository implements InstitutionRepository {
    @Inject
    public JPAInstitutionRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public static List<Institution> list(EntityManager em, String email) {
        return em.createQuery("SELECT i FROM Institution i JOIN i.users u WHERE u.email LIKE :userEmail", Institution.class)
                .setParameter("userEmail", email)
                .getResultList();
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

    public static boolean delete(EntityManager em, long institutionId) {
        Institution institution = em.find(Institution.class, institutionId);
        em.remove(institution);
        return true;
    }

    public static Institution getByName(EntityManager em, String name) {
        return em.createQuery("select p from Institution p where name=:nameparam", Institution.class)
                .setParameter("nameparam", name)
                .getSingleResult();
    }

    public static boolean hasUser(EntityManager em, long institutionId, String userEmail) {
        try {
            return em
                    .createQuery("SELECT i FROM Institution i " +
                            "JOIN i.users u " +
                            "WHERE i.id = :institutionId and " +
                            "u.email LIKE :userEmail")
                    .setParameter("institutionId", institutionId)
                    .setParameter("userEmail", userEmail)
                    .getResultList()
                    .size() > 0;
        } catch (NoResultException e) {
            return false;
        }
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
    public void delete(long institutionId) {
        wrap(em -> delete(em, institutionId));
    }

    @Override
    public List<Institution> list(String email) {
        return wrap(em -> list(em, email));
    }

    @Override
    public List<Institution> list() {
        return wrap(em -> list(em));
    }

    @Override
    public Institution getByName(String name) {
        return wrap(em -> getByName(em, name));
    }

    @Override
    public boolean hasUser(long institutionId, String userEmail) {
        return wrap(em -> hasUser(em, institutionId, userEmail));
    }

    @Override
    public Institution update(Institution institution) {
        return wrap(em -> update(em, institution));
    }

    private Institution update(EntityManager em, Institution institution) {
        em.merge(institution);
        return institution;
    }
}
