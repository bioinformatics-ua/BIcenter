package models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class StepProperty implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String field;
    private String value;
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    private Step step;

    public StepProperty(){ this.date = new Date(); }
    public StepProperty(String field, String value) {
        this.field = field;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

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
}