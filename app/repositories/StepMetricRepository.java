package repositories;

import com.google.inject.ImplementedBy;
import models.StepMetric;

import java.util.List;

@ImplementedBy(JPAStepMetricRepository.class)
public interface StepMetricRepository {
    StepMetric get(long id);

    StepMetric add(StepMetric stepMetric);

    List<StepMetric> list();

    StepMetric getByExecutionAndStepLabel(long executionId, String stepLabel);
}