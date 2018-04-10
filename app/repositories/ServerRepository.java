package repositories;

import com.google.inject.ImplementedBy;
import models.Server;

import java.util.List;

@ImplementedBy(JPAServerRepository.class)
public interface ServerRepository {
    Server get(long id);

    Server add(Server server);

    void delete(long id);

    List<Server> list();

    Server getByName(String name);
}
