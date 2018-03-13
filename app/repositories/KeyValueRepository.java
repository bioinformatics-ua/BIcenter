package repositories;

import com.google.inject.ImplementedBy;
import models.KeyValue;

import java.util.List;

@ImplementedBy(JPAKeyValueRepository.class)
public interface KeyValueRepository {
    KeyValue get(long id);

    KeyValue add(KeyValue stepStatus);

    List<KeyValue> list();

    void addAll(List<KeyValue> keyValues);
}
