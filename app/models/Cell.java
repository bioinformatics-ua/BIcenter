package models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Cell implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int x;
    private int y;

    private int width;
    private int height;

    @OneToOne(fetch= FetchType.LAZY)
    private Step step;

    public Cell(){}
    public Cell(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cell cell = (Cell) o;

        if (x != cell.x) return false;
        if (y != cell.y) return false;
        if (width != cell.width) return false;
        if (height != cell.height) return false;
        if (id != null ? !id.equals(cell.id) : cell.id != null) return false;
        return step != null ? step.equals(cell.step) : cell.step == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + (step != null ? step.hashCode() : 0);
        return result;
    }
}
