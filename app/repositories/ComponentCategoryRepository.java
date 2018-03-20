package repositories;

import com.google.inject.ImplementedBy;
import models.ComponentCategory;

import java.util.List;

@ImplementedBy(JPAComponentCategoryRepository.class)
public interface ComponentCategoryRepository {
    ComponentCategory get(String name);

    ComponentCategory add(ComponentCategory componentCategory);

    List<ComponentCategory> list();
}
