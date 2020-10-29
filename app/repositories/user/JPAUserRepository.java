package repositories.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import models.rbac.User;
import play.db.jpa.JPAApi;
import repositories.DatabaseExecutionContext;
import repositories.JPARepository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class JPAUserRepository extends JPARepository implements UserRepository {
    @Inject
    public JPAUserRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public static User findByEmail(EntityManager em, String email) {
        try {
            return em.createQuery("SELECT u FROM User u where u.email = :email", User.class)
                    .setParameter("email", email)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public CompletionStage<User> getAsync(long id) {
        return CompletableFuture.supplyAsync(() -> wrap(em -> get(em, id)), executionContext);
    }

    @Override
    public User get(long id) {
        return wrap(em -> get(em, id));
    }

    @Override
    public User findByEmail(String email) {
        return wrap(em -> findByEmail(em, email));
    }

    @Override
    public User add(User user) {
        return wrap(em -> create(em, user));
    }

    @Override
    public CompletionStage<User> addAsync(User user) {
        return CompletableFuture.supplyAsync(() -> wrap(em -> create(em, user)), executionContext);
    }

    @Override
    public CompletionStage<User> updateAsync(User user) {
        return CompletableFuture.supplyAsync(() -> wrap(em -> update(em, user)), executionContext);
    }

    @Override
    public User update(User user) {
        return wrap(em -> update(em, user));
    }

    private User get(EntityManager em, long id) {
        return em.find(User.class, id);
    }

    public User create(EntityManager em, User user) {
        if(findByEmail(user.getEmail()) == null) {
            em.persist(user);
        }
        return user;
    }

    private User update(EntityManager em, User user) {
        em.merge(user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return wrap(this::findAll);
    }

    private List<User> findAll(EntityManager em) {
        try {
            return em.createQuery("SELECT u FROM User u", User.class)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
