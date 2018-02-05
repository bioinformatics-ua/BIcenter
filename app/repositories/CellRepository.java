package repositories;

import com.google.inject.ImplementedBy;
import models.Cell;

import java.util.List;

@ImplementedBy(JPACellRepository.class)
public interface CellRepository {
    Cell get(long id);

    Cell add(Cell Cell);

    List<Cell> list();
}
