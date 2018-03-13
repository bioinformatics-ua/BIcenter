package repositories.user;

import com.google.inject.ImplementedBy;
import models.rbac.User;

import java.util.concurrent.CompletionStage;

@ImplementedBy(JPAUserRepository.class)
public interface UserRepository {
    CompletionStage<User> getAsync(long id);

    User get(long id);

    User findByEmail(String email);

    CompletionStage<User> addAsync(User user);

    User add(User user);

    CompletionStage<User> updateAsync(User user);

    User update(User user);
}
