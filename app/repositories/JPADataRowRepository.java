package repositories;

import com.google.inject.Inject;
import models.DataRow;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import java.util.List;

public class JPADataRowRepository extends JPARepository implements DataRowRepository {
    @Inject
    public JPADataRowRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public static List<DataRow> list(EntityManager em) {
        return em.createQuery("select p from DataRow p", DataRow.class).getResultList();
    }

    public static DataRow get(EntityManager em, long id) {
        return em.find(DataRow.class, id);
    }

    public static DataRow createOrUpdate(EntityManager em, DataRow dataRow) {
        dataRow = em.merge(dataRow);
        return dataRow;
    }

    @Override
    public DataRow get(long id) {
        return wrap(em -> get(em, id));
    }

    @Override
    public DataRow add(DataRow dataRow) {
        return wrap(em -> createOrUpdate(em, dataRow));
    }

    @Override
    public List<DataRow> list() {
        return wrap(em -> list(em));
    }
}
