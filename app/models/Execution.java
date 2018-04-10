package models;

import models.rbac.User;
import org.joda.time.Interval;
import org.joda.time.Period;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Entity
public class Execution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Server server;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "execution", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<StepMetric> stepMetrics;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "execution", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<Status> stepStatus;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "execution", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<DataRow> dataRows;

    private boolean finished;
    private int errors;
    @Lob
    private String log;

    private Date startDate;
    private Date endDate;
    @Column(name = "duration", length = 2000)
    private Period duration;

    public Execution() {
        this.finished = false;
        this.startDate = new Date();
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Task getTask() { return task; }

    public void setTask(Task task) { this.task = task; }

    public List<StepMetric> getStepMetrics() { return stepMetrics; }

    public void setStepMetrics(List<StepMetric> stepMetrics) { this.stepMetrics = stepMetrics; }

    public List<Status> getStepStatus() { return stepStatus; }

    public void setStepStatus(List<Status> stepStatus) { this.stepStatus = stepStatus; }

    public List<DataRow> getDataRows() { return dataRows; }

    public void setDataRows(List<DataRow> dataRows) { this.dataRows = dataRows; }

    public boolean isFinished() { return finished; }

    public void setFinished(boolean finished) { this.finished = finished; }

    public int getErrors() {
        return errors;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }

    public void setDuration(Period duration) {
        this.duration = duration;
    }

    public String getLog() { return log; }

    public void setLog(String log) { this.log = log; }

    public Date getStartDate() { return startDate; }

    public void setStartDate(Date date) { this.startDate = date; }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
        Interval interval = new Interval(startDate.getTime(), endDate.getTime());
        duration = interval.toPeriod();
    }

    public Period getDuration(){
        return duration;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}
