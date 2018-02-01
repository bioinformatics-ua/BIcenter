package repositories;

import com.google.inject.ImplementedBy;
import models.ComponentMetadata;
import java.util.List;

@ImplementedBy(JPAComponentMetadataRepository.class)
public interface ComponentMetadataRepository {
    ComponentMetadata get(long id);
    ComponentMetadata add(ComponentMetadata component);
    List<ComponentMetadata> list();
}