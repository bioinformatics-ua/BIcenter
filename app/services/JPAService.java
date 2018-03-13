package services;

import com.google.inject.Inject;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import java.util.function.Function;

public class JPAService {
    private final JPAApi jpaApi;

    @Inject
    public JPAService(JPAApi jpaApi) {
        this.jpaApi = jpaApi;
    }

    protected <T> T withTransaction(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
