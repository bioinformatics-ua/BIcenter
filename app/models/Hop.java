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

    @ManyToOne(fetch= FetchType.LAZY)
    private Step source;
    @ManyToOne(fetch= FetchType.LAZY)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hop hop = (Hop) o;

        if (graphId != hop.graphId) return false;
        if (id != null ? !id.equals(hop.id) : hop.id != null) return false;
        if (source != null ? !source.equals(hop.source) : hop.source != null) return false;
        if (destiny != null ? !destiny.equals(hop.destiny) : hop.destiny != null) return false;
        if (taskHops != null ? !taskHops.equals(hop.taskHops) : hop.taskHops != null) return false;
        return date != null ? date.equals(hop.date) : hop.date == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + graphId;
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (destiny != null ? destiny.hashCode() : 0);
        result = 31 * result + (taskHops != null ? taskHops.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
