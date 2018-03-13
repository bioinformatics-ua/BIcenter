package repositories.user;

import com.google.inject.ImplementedBy;
import configuration.CRole;
import models.rbac.Category;
import models.rbac.Operation;
import models.rbac.Permission;
import models.rbac.Role;

import java.util.List;

@ImplementedBy(JPARBACRepository.class)
public interface RBACRepository {
    Operation addOperation(Operation operation);

    Operation findOperation(String name);

    Category addCategory(Category category);

    Category findCategory(String name);

    Permission addPermission(Permission permission);

    Role addRole(Role role);

    Role findRole(String name);

    List<Role> findAllRoles();

    boolean initializeRoles(List<String> operations, List<String> categories, List<CRole> roles);
}
