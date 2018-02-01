package repositories;

import com.google.inject.ImplementedBy;
import models.ComponentProperty;
import java.util.List;

@ImplementedBy(JPAComponentPropertyRepository.class)
public interface ComponentPropertyRepository {
    ComponentProperty get(long id);
    ComponentProperty add(ComponentProperty componentProperty);
    List<ComponentProperty> list();
}
