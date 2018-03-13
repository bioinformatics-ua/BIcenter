package repositories;

import com.google.inject.ImplementedBy;
import models.Status;

import java.util.List;

@ImplementedBy(JPAStatusRepository.class)
public interface StatusRepository {
    Status get(long id);

    Status add(Status stepStatus);

    List<Status> list();

    Status getByExecutionAndStepLabel(long executionId, String stepLabel);
}
