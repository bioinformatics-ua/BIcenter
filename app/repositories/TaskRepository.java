package repositories;

import com.google.inject.ImplementedBy;
import models.Task;

import java.util.List;

@ImplementedBy(JPATaskRepository.class)
public interface TaskRepository {
    Task get(long id);

    Task add(Task Task);

    List<Task> list();

    Task getByName(String name);

    Task getByInstitutionAndName(long institutionId, String name);

    List<String> getOpenTabs();

    void delete(long task);
}
