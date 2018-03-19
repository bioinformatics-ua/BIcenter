package repositories;

import com.google.inject.Inject;
import models.Server;
import models.Status;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import java.util.List;

public class JPAServerRepository extends JPARepository implements ServerRepository {
    @Inject
    public JPAServerRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public static List<Server> list(EntityManager em) {
        return em.createQuery("select p from Server p", Server.class).getResultList();
    }

    public static Server get(EntityManager em, long id) { return em.find(Server.class, id); }

    public static Server createOrUpdate(EntityManager em, Server server) {
        server = em.merge(server);
        return server;
    }

    public static Server getByName(EntityManager em, String name){
        return em.createQuery("select p from Server p where name=:nameparam", Server.class)
                .setParameter("nameparam",name)
                .getSingleResult();
    }

    @Override
    public Server get(long id) {
        return wrap(em -> get(em, id));
    }

    @Override
    public Server add(Server server) {
        return wrap(em -> createOrUpdate(em, server));
    }

    @Override
    public List<Server> list() {
        return wrap(em -> list(em));
    }

    @Override
    public Server getByName(String name) { return wrap(em -> getByName(em,name)); }
}
