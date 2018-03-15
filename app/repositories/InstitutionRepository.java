package repositories;

import com.google.inject.ImplementedBy;
import models.Hop;
import models.Institution;

import java.util.List;

@ImplementedBy(JPAInstitutionRepository.class)
public interface InstitutionRepository {
    Institution get(long id);

    Institution add(Institution institution);

    void delete(Institution institution);

    List<Institution> list();
}
