package repositories;

import com.google.inject.ImplementedBy;
import models.DataSource;

import java.util.List;

@ImplementedBy(JPADataSourceRepository.class)
public interface DataSourceRepository {
    DataSource get(long id);

    DataSource add(DataSource dataSource);

    List<DataSource> list();

    DataSource getByName(String name);
}
