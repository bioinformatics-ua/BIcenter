package repositories;

import com.google.inject.Inject;
import models.KeyValue;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import java.util.List;

public class JPAKeyValueRepository extends JPARepository implements KeyValueRepository {
    @Inject
    public JPAKeyValueRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public static List<KeyValue> list(EntityManager em) {
        return em.createQuery("select p from KeyValue p", KeyValue.class).getResultList();
    }

    public static KeyValue get(EntityManager em, long id) {
        return em.find(KeyValue.class, id);
    }

    public static KeyValue createOrUpdate(EntityManager em, KeyValue keyValue) {
        keyValue = em.merge(keyValue);
        return keyValue;
    }

    @Override
    public KeyValue get(long id) {
        return wrap(em -> get(em, id));
    }

    @Override
    public KeyValue add(KeyValue keyValue) {
        return wrap(em -> createOrUpdate(em, keyValue));
    }

    @Override
    public List<KeyValue> list() {
        return wrap(em -> list(em));
    }

    @Override
    public void addAll(List<KeyValue> keyValues) {
        wrap(em -> {
            for (KeyValue kv : keyValues) {
                createOrUpdate(em, kv);
            }

            return true;
        });
    }
}
