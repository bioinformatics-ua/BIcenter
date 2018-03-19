package repositories;

import com.google.inject.ImplementedBy;
import models.Server;

import java.util.List;

@ImplementedBy(JPAServerRepository.class)
public interface ServerRepository {
    Server get(long id);

    Server add(Server server);

    List<Server> list();

    Server getByName(String name);
}
