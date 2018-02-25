package models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
public class Step implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;
    private int graphId;
    private Date date;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "step", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<StepProperty> stepProperties;

    @ManyToOne(fetch = FetchType.LAZY)
    private Component component;

    @ManyToOne(fetch = FetchType.LAZY)
    private Task taskSteps;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "step", cascade = CascadeType.ALL)
    private Cell cell;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "source", cascade = CascadeType.ALL)
    private Hop source;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "destiny", cascade = CascadeType.ALL)
    private Hop destiny;

    public Step() { date = new Date(); }
    public Step(String label, int graphId) {
        this.label = label; this.graphId = graphId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getGraphId() { return graphId; }

    public void setGraphId(int graphId) { this.graphId = graphId; }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<StepProperty> getStepProperties() {
        return stepProperties;
    }

    public void setStepProperties(List<StepProperty> stepProperties) {
        this.stepProperties = stepProperties;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public Task getTaskSteps() { return taskSteps; }

    public void setTaskSteps(Task taskSteps) { this.taskSteps = taskSteps; }

    public Cell getCell() { return cell; }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public Hop getSource() {
        return source;
    }

    public void setSource(Hop source) {
        this.source = source;
    }

    public Hop getDestiny() {
        return destiny;
    }

    public void setDestiny(Hop destiny) {
        this.destiny = destiny;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Step step = (Step) o;

        if (graphId != step.graphId) return false;
        if (id != null ? !id.equals(step.id) : step.id != null) return false;
        if (label != null ? !label.equals(step.label) : step.label != null) return false;
        if (date != null ? !date.equals(step.date) : step.date != null) return false;
        if (stepProperties != null ? !stepProperties.equals(step.stepProperties) : step.stepProperties != null)
            return false;
        if (component != null ? !component.equals(step.component) : step.component != null) return false;
        if (taskSteps != null ? !taskSteps.equals(step.taskSteps) : step.taskSteps != null) return false;
        if (cell != null ? !cell.equals(step.cell) : step.cell != null) return false;
        if (source != null ? !source.equals(step.source) : step.source != null) return false;
        return destiny != null ? destiny.equals(step.destiny) : step.destiny == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + graphId;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (stepProperties != null ? stepProperties.hashCode() : 0);
        result = 31 * result + (component != null ? component.hashCode() : 0);
        result = 31 * result + (taskSteps != null ? taskSteps.hashCode() : 0);
        result = 31 * result + (cell != null ? cell.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (destiny != null ? destiny.hashCode() : 0);
        return result;
    }
}
