package models;

import javax.persistence.*;

@Entity
public class StepMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Execution execution;
    @ManyToOne(fetch = FetchType.LAZY)
    private Step step;

    private String nRecords;
    private String colRead;
    private String colWrite;
    private String colEnter;
    private String colOutput;
    private String colUpdate;
    private String colRefuse;
    private String colError;
    private String colState;
    private String colTime;
    private String colSpeed;
    private String colPriInOut;

    public StepMetric() { }
    public StepMetric(String[] performanceMetrics) {
        this.nRecords = performanceMetrics[2];
        this.colRead = performanceMetrics[3];
        this.colWrite = performanceMetrics[4];
        this.colEnter = performanceMetrics[5];
        this.colOutput = performanceMetrics[6];
        this.colUpdate = performanceMetrics[7];
        this.colRefuse = performanceMetrics[8];
        this.colError = performanceMetrics[9];
        this.colState = performanceMetrics[10];
        this.colTime = performanceMetrics[11];
        this.colSpeed = performanceMetrics[12];
        this.colPriInOut = performanceMetrics[13];
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Execution getExecution() { return execution; }

    public void setExecution(Execution execution) { this.execution = execution; }

    public Step getStep() { return step; }

    public void setStep(Step step) { this.step = step; }

    public String getnRecords() { return nRecords; }

    public void setnRecords(String nRecords) { this.nRecords = nRecords; }

    public String getColRead() { return colRead; }

    public void setColRead(String read) { this.colRead = read; }

    public String getColWrite() { return colWrite; }

    public void setColWrite(String write) { this.colWrite = write; }

    public String getColEnter() { return colEnter; }

    public void setColEnter(String enter) { this.colEnter = enter; }

    public String getColOutput() { return colOutput; }

    public void setColOutput(String output) { this.colOutput = output; }

    public String getColUpdate() { return colUpdate; }

    public void setColUpdate(String colUpdate) { this.colUpdate = colUpdate; }

    public String getColRefuse() { return colRefuse; }

    public void setColRefuse(String refuse) { this.colRefuse = refuse; }

    public String getColError() { return colError; }

    public void setColError(String error) { this.colError = error; }

    public String getColState() { return colState; }

    public void setColState(String state) { this.colState = state; }

    public String getColTime() { return colTime; }

    public void setColTime(String time) { this.colTime = time; }

    public String getColSpeed() { return colSpeed; }

    public void setColSpeed(String speed) { this.colSpeed = speed; }

    public String getColPriInOut() { return colPriInOut; }

    public void setColPriInOut(String priInOut) { this.colPriInOut = priInOut; }
}
