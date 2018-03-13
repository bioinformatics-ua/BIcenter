package services.rbac;

import com.google.inject.Inject;
import models.rbac.Role;
import models.rbac.User;
import org.mindrot.jbcrypt.BCrypt;
import play.db.jpa.JPAApi;
import repositories.user.JPARBACRepository;
import repositories.user.JPAUserRepository;
import services.JPAService;

import java.util.ArrayList;
import java.util.List;

public class UserService extends JPAService {
    private final JPAUserRepository userRepository;
    private final JPARBACRepository rbacRepository;

    @Inject
    public UserService(JPAApi jpaApi, JPAUserRepository userRepository, JPARBACRepository rbacRepository) {
        super(jpaApi);
        this.userRepository = userRepository;
        this.rbacRepository = rbacRepository;
    }

    public User get(long id) {
        return userRepository.get(id);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User authenticate(String username, String password) {
        try {
            User user = userRepository.findByEmail(username);

            // Only authenticate local users
            if (user == null || user.getType() != User.UserType.Local) {
                return null;
            }

            if (BCrypt.checkpw(password, user.getPassword())) {
                return user;
            }

            return null;
        } catch (Exception ex) {
            // No user found, returns null
            return null;
        }
    }

    public User createUserWithRoles(User user, List<String> roles) {
        return withTransaction(em -> {
            List<Role> _roles = new ArrayList<>();

            for (String r : roles) {
                Role role = rbacRepository.findRole(em, r);
                if (role == null) {
                    System.err.println("User role not found: " + r);
                    continue;
                }

                _roles.add(role);
            }

            user.getRoles().addAll(_roles);
            userRepository.create(em, user);

            return user;
        });
    }

}
