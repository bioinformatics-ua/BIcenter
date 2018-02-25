package models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class StepProperty implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    private ComponentProperty componentProperty;

    @Lob
    private String value;
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    private Step step;

    public StepProperty(){ this.date = new Date(); }
    public StepProperty(String value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ComponentProperty getComponentProperty() { return componentProperty; }

    public void setComponentProperty(ComponentProperty componentProperty) { this.componentProperty = componentProperty; }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

        StepProperty that = (StepProperty) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (componentProperty != null ? !componentProperty.equals(that.componentProperty) : that.componentProperty != null)
            return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        return step != null ? step.equals(that.step) : that.step == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (componentProperty != null ? componentProperty.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (step != null ? step.hashCode() : 0);
        return result;
    }
}
