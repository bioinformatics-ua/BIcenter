package models;

import javax.persistence.*;
import java.util.List;

@Entity
public class DataRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Execution execution;
    @ManyToOne(fetch = FetchType.LAZY)
    private Step step;

    private int nrRow;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dataRow", cascade = CascadeType.ALL)
    private List<KeyValue> keyValues;

    public DataRow() { }
    public DataRow(int nrRow) {
        this.nrRow = nrRow;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Execution getExecution() { return execution; }

    public void setExecution(Execution execution) { this.execution = execution; }

    public Step getStep() { return step; }

    public void setStep(Step step) { this.step = step; }

    public int getNrRow() { return nrRow; }

    public void setNrRow(int nrRow) { this.nrRow = nrRow; }

    public List<KeyValue> getKeyValues() { return keyValues; }

    public void setKeyValues(List<KeyValue> keyValues) { this.keyValues = keyValues; }
}
