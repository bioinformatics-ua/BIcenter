package models;

import javax.persistence.*;

@Entity
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Execution execution;
    @ManyToOne(fetch = FetchType.LAZY)
    private Step step;

    private Integer status;
    @Lob
    private String logText;

    public Status() { }
    public Status(Integer status, String logText) {
        this.status = status;
        this.logText = logText;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Execution getExecution() { return execution; }

    public void setExecution(Execution execution) { this.execution = execution; }

    public Step getStep() { return step; }

    public void setStep(Step step) { this.step = step; }

    public Integer getStatus() { return status; }

    public void setStatus(Integer status) { this.status = status; }

    public String getLogText() { return logText; }

    public void setLogText(String logText) { this.logText = logText; }
}
