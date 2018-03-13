package repositories.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import configuration.CPermission;
import configuration.CRole;
import models.rbac.Category;
import models.rbac.Operation;
import models.rbac.Permission;
import models.rbac.Role;
import play.db.jpa.JPAApi;
import repositories.DatabaseExecutionContext;
import repositories.JPARepository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

@Singleton
public class JPARBACRepository extends JPARepository implements RBACRepository {
    @Inject
    public JPARBACRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    @Override
    public Operation addOperation(Operation operation) {
        return wrap(em -> createOperation(em, operation));
    }

    @Override
    public Operation findOperation(String name) {
        return wrap(em -> findOperation(em, name));
    }

    @Override
    public Category addCategory(Category category) {
        return wrap(em -> createCategory(em, category));
    }

    @Override
    public Category findCategory(String name) {
        return wrap(em -> findCategory(em, name));
    }

    @Override
    public Permission addPermission(Permission permission) {
        return wrap(em -> createPermission(em, permission));
    }

    @Override
    public Role addRole(Role role) {
        return wrap(em -> createRole(em, role));
    }

    @Override
    public Role findRole(String name) {
        return wrap(em -> findRole(em, name));
    }

    @Override
    public List<Role> findAllRoles() {
        return wrap(em -> findAllRoles(em));
    }

    @Override
    public boolean initializeRoles(final List<String> operations, final List<String> categories, final List<CRole> roles) {
        return wrap(em -> {
            // Add operations
            for (String name : operations) {
                Operation operation = new Operation(name);
                createOperation(em, operation);
            }

            // Add categories
            for (String name : categories) {
                Category category = new Category(name);
                createCategory(em, category);
            }

            // Add roles
            for (CRole r : roles) {

                Role role = new Role(r.getName());

                for (CPermission p : r.getPermissions()) {
                    // Find category
                    Category category = findCategory(em, p.getCategory());
                    if (category == null) {
                        System.err.println("Category not found: " + p.getCategory());
                        continue;
                    }

                    for (String o : p.getOperations()) {
                        // Find operation
                        Operation operation = findOperation(em, o);
                        if (operation == null) {
                            System.err.println("Operations not found: " + o);
                            continue;
                        }

                        // Add permission to role
                        Permission permission = new Permission(operation, category);
                        createPermission(em, permission);
                        role.addPermission(permission);
                    }
                }

                createRole(em, role);
            }

            return true;
        });
    }

    private Operation createOperation(EntityManager em, Operation operation) {
        em.persist(operation);
        return operation;
    }

    private Operation findOperation(EntityManager em, String name) {
        try {
            return em.createQuery("SELECT o FROM Operation o WHERE o.name=:name", Operation.class)
                    .setParameter("name", name)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private Category createCategory(EntityManager em, Category category) {
        em.persist(category);
        return category;
    }

    private Category findCategory(EntityManager em, String name) {
        try {
            return em.createQuery("SELECT c FROM Category c WHERE c.name=:name", Category.class)
                    .setParameter("name", name)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private Permission createPermission(EntityManager em, Permission permission) {
        em.persist(permission);
        return permission;
    }

    private Role createRole(EntityManager em, Role role) {
        em.persist(role);
        return role;
    }

    public Role findRole(EntityManager em, String name) {
        try {
            return em.createQuery("SELECT r FROM Role r WHERE r.name=:name", Role.class)
                    .setParameter("name", name)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private List<Role> findAllRoles(EntityManager em) {
        try {
            return em.createQuery("SELECT r FROM Role r", Role.class)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
