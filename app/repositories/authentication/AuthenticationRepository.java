package repositories.authentication;

import com.google.inject.ImplementedBy;
import models.authentication.Authentication;

import java.util.List;

@ImplementedBy(JPAAuthenticationRepository.class)
public interface AuthenticationRepository {
    Authentication get(long id);

    Authentication add(Authentication authentication);

    Authentication update(Authentication authentication);

    List<Authentication> findAll();
}
