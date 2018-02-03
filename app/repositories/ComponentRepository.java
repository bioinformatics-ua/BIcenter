package repositories;

import com.google.inject.ImplementedBy;
import models.Component;

import java.util.List;

@ImplementedBy(JPAComponentRepository.class)
public interface ComponentRepository {
    Component get(long id);

    Component add(Component component);

    List<Component> list();

    Component getByName(String name);
}
