package repositories;

import com.google.inject.ImplementedBy;
import models.Step;

import java.util.List;

@ImplementedBy(JPAStepRepository.class)
public interface StepRepository {
    Step get(long id);

    Step add(Step Step);

    List<Step> list();
}
