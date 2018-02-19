package repositories;

import com.google.inject.ImplementedBy;
import models.Step;
import models.StepProperty;

import java.util.List;

@ImplementedBy(JPAStepRepository.class)
public interface StepRepository {
    Step get(long id);

    Step add(Step Step);

    void delete(Step step);

    List<Step> list();

    Step getByTaskAndGraphId(long taskId, int graphId);

    List<Step> getByTask(long taskId);
}
