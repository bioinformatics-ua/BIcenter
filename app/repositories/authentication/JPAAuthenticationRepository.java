package repositories.authentication;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import models.authentication.Authentication;
import play.db.jpa.JPAApi;
import repositories.DatabaseExecutionContext;
import repositories.JPARepository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

@Singleton
public class JPAAuthenticationRepository extends JPARepository implements AuthenticationRepository {
    @Inject
    public JPAAuthenticationRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    @Override
    public Authentication get(long id) {
        return wrap(em -> get(em, id));
    }

    @Override
    public Authentication add(Authentication authentication) {
        return wrap(em -> create(em, authentication));
    }

    @Override
    public Authentication update(Authentication authentication) {
        return wrap(em -> update(em, authentication));
    }

    @Override
    public List<Authentication> findAll() {
        return wrap(em -> findAll(em));
    }

    private Authentication get(EntityManager em, long id) {
        return em.find(Authentication.class, id);
    }

    public Authentication create(EntityManager em, Authentication authentication) {
        em.persist(authentication);
        return authentication;
    }

    private Authentication update(EntityManager em, Authentication authentication) {
        em.merge(authentication);
        return authentication;
    }

    private List<Authentication> findAll(EntityManager em) {
        try {
            return em.createQuery("SELECT A FROM Authentication A", Authentication.class)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}