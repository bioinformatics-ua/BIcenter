package repositories;

import com.google.inject.ImplementedBy;
import models.StepProperty;

import java.util.List;

@ImplementedBy(JPAStepPropertyRepository.class)
public interface StepPropertyRepository {
    StepProperty get(long id);

    StepProperty add(StepProperty StepProperty);

    List<StepProperty> list();

    List<StepProperty> getByComponentProperty(long componentPropertyId);

    StepProperty getByStepAndComponentProperty(long stepId, long componentPropertyId);
}
