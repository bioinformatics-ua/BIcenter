package repositories;

import com.google.inject.Inject;
import models.DataSource;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import java.util.List;

public class JPADataSourceRepository extends JPARepository implements DataSourceRepository {
    @Inject
    public JPADataSourceRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public static List<DataSource> list(EntityManager em) {
        return em.createQuery("select p from DataSource p", DataSource.class).getResultList();
    }

    public static DataSource get(EntityManager em, long id) {
        return em.find(DataSource.class, id);
    }

    public static DataSource createOrUpdate(EntityManager em, DataSource dataSource) {
        dataSource = em.merge(dataSource);
        return dataSource;
    }

    public static DataSource getByName(EntityManager em, String name){
        return em.createQuery("select p from DataSource p where connectionName=:nameparam", DataSource.class)
                .setParameter("nameparam",name)
                .getSingleResult();
    }

    @Override
    public DataSource get(long id) {
        return wrap(em -> get(em, id));
    }

    @Override
    public DataSource add(DataSource dataSource) {
        return wrap(em -> createOrUpdate(em, dataSource));
    }

    @Override
    public List<DataSource> list() {
        return wrap(em -> list(em));
    }

    @Override
    public DataSource getByName(String name) { return wrap(em -> getByName(em,name)); }
}
