package models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
public abstract class Step implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;
    private Date date;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "step", cascade = CascadeType.ALL)
    private List<StepProperty> stepProperties;

    @ManyToOne(fetch = FetchType.LAZY)
    private Component component;

    @ManyToOne(fetch = FetchType.LAZY)
    private Task taskSteps;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "step", cascade = CascadeType.ALL)
    private Cell cell;

    @OneToOne(fetch= FetchType.LAZY)
    private Hop source;

    @OneToOne(fetch= FetchType.LAZY)
    private Hop destiny;

    public Step() { date = new Date(); }
    public Step(String label) {
        this.label = label;
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
}
