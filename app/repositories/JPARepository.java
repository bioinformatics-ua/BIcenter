package repositories;

import com.google.inject.Inject;
import play.db.jpa.JPAApi;


import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import java.util.function.Function;

public class JPARepository {
    private final JPAApi jpaApi;
    protected final DatabaseExecutionContext executionContext;

    @Inject
    public JPARepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    protected <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
