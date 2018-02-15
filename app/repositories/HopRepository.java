package repositories;

import com.google.inject.ImplementedBy;
import models.Hop;

import java.util.List;

@ImplementedBy(JPAHopRepository.class)
public interface HopRepository {
    Hop get(long id);

    Hop add(Hop Hop);

    void delete(Hop hop);

    List<Hop> list();

    List<Hop> getByTask(long taskId);
}
