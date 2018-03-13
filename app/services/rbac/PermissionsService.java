package services.rbac;

import com.google.inject.Inject;
import models.rbac.Role;
import models.rbac.User;
import repositories.user.UserRepository;

public class PermissionsService {
    private final UserRepository userRepository;

    @Inject
    public PermissionsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean hasPermission(String username, String[] needs, String categoryname) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            return false;
        }

        for (String need : needs) {
            for (Role role : user.getRoles()) {
                if (role.hasPermission(need, categoryname)) {
                    return true;
                }
            }
        }

        return false;
    }
}
