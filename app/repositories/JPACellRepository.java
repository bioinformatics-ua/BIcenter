package repositories;

import com.google.inject.Inject;
import models.Cell;
import play.db.jpa.JPAApi;

import javax.persistence.EntityManager;
import java.util.List;

public class JPACellRepository extends JPARepository implements CellRepository {
    @Inject
    public JPACellRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        super(jpaApi, executionContext);
    }

    public static List<Cell> list(EntityManager em) {
        return em.createQuery("select p from Cell p", Cell.class).getResultList();
    }

    public static Cell get(EntityManager em, long id) {
        return em.find(Cell.class, id);
    }

    public static Cell createOrUpdate(EntityManager em, Cell cell) {
        em.persist(cell);
        return cell;
    }

    public static Cell getByStep(EntityManager em, long stepId) {
        return em.createQuery("select p from Cell p where step_id=:stepId", Cell.class)
                .setParameter("stepId",stepId)
                .getSingleResult();
    }

    @Override
    public Cell get(long id) {
        return wrap(em -> get(em, id));
    }

    @Override
    public Cell add(Cell Cell) {
        return wrap(em -> createOrUpdate(em, Cell));
    }

    @Override
    public List<Cell> list() {
        return wrap(em -> list(em));
    }

    @Override
    public Cell getByStep(long stepId) { return wrap(em -> getByStep(em,stepId)); }
}


