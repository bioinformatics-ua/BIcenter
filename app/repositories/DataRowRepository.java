package repositories;

import com.google.inject.ImplementedBy;
import models.DataRow;

import java.util.List;

@ImplementedBy(JPADataRowRepository.class)
public interface DataRowRepository {
    DataRow get(long id);

    DataRow add(DataRow stepMetric);

    List<DataRow> list();
}
