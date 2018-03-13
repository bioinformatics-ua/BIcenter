package repositories;

import com.google.inject.ImplementedBy;
import models.Execution;

import java.util.List;

@ImplementedBy(JPAExecutionRepository.class)
public interface ExecutionRepository {
    Execution get(long id);

    Execution add(Execution execution);

    List<Execution> list();
}
