/*
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
    private Task task;

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

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
*/
