package models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Hop implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int graphId;

    @OneToOne(fetch= FetchType.LAZY)
    private Step source;
    @OneToOne(fetch= FetchType.LAZY)
    private Step destiny;

    @ManyToOne(fetch = FetchType.LAZY)
    private Task taskHops;

    private Date date;

    public Hop() {
        this.date = new Date();
    }

    public Hop(int graphId) { this.graphId = graphId; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getGraphId() { return graphId; }

    public void setGraphId(int graphId) { this.graphId = graphId; }

    public Step getSource() {
        return source;
    }

    public void setSource(Step source) {
        this.source = source;
    }

    public Step getDestiny() {
        return destiny;
    }

    public void setDestiny(Step destiny) {
        this.destiny = destiny;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Task getTaskHops() { return taskHops; }

    public void setTaskHops(Task taskHops) { this.taskHops = taskHops; }
}
