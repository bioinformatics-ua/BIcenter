package services.rbac;

import com.google.inject.Inject;
import models.Institution;
import models.rbac.Role;
import models.rbac.User;
import org.mindrot.jbcrypt.BCrypt;
import play.db.jpa.JPAApi;
import repositories.JPAInstitutionRepository;
import repositories.user.JPARBACRepository;
import repositories.user.JPAUserRepository;
import services.JPAService;

import java.util.ArrayList;
import java.util.List;

public class UserService extends JPAService {
	private final JPAUserRepository userRepository;
	private final JPARBACRepository rbacRepository;
	private final JPAInstitutionRepository institutionRepository;

	@Inject
	public UserService(JPAApi jpaApi, JPAUserRepository userRepository, JPARBACRepository rbacRepository, JPAInstitutionRepository institutionRepository) {
		super(jpaApi);
		this.userRepository = userRepository;
		this.rbacRepository = rbacRepository;
		this.institutionRepository = institutionRepository;
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

	public User createUserWithRolesAndInstitutions(User user, List<String> roles, List<String> institutions) {
		return withTransaction(em -> {
			List<Role> _roles = new ArrayList<>();
			List<Institution> _institutions = new ArrayList<>();

			for (String r : roles) {
				Role role = rbacRepository.findRole(em, r);
				if (role == null) {
					System.err.println("User role not found: " + r);
					continue;
				}
				_roles.add(role);
			}

			for (String r : institutions) {
				Institution institution = institutionRepository.findInstitution(em, r);
				System.out.println(institution.getName());
				if (institution == null) {
					System.err.println("User institutions not found: " + r);
					continue;
				}
				_institutions.add(institution);
			}

			user.getRoles().addAll(_roles);
			user.getInstitutions().addAll(_institutions);
			userRepository.create(em, user);

			return user;
		});
	}

}
