package models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Schedule implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;

    private Date start;
    private String period;

    @ManyToOne(fetch= FetchType.LAZY)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    private Institution institution;

    @ManyToOne(fetch = FetchType.LAZY)
    private Server server;

    public Schedule() { date = new Date(); }
    public Schedule(Date start, String interval) {
        this.start = start;
        this.period = interval;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public String getInterval() {
        return period;
    }

    public void setInterval(String interval) {
        this.period = interval;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}
