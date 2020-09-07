package repositories;

import com.google.inject.ImplementedBy;
import models.Hop;
import models.Institution;
import models.rbac.User;

import java.util.List;

@ImplementedBy(JPAInstitutionRepository.class)
public interface InstitutionRepository {
    Institution get(long id);

    Institution add(Institution institution);

    void delete(long institutionId);

    List<Institution> list(String email);

    List<Institution> list();

    Institution getByName(String name);

    Institution update(Institution institution);

    boolean hasUser(long institutionId, String userEmail);
}
