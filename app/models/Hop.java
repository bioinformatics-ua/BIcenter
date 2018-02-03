/*
package models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
public class Hop implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "hop", cascade = CascadeType.ALL)
    @MapsId
    private Step source;
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "hop", cascade = CascadeType.ALL)
    @MapsId
    private Step destiny;

    @ManyToOne(fetch = FetchType.LAZY)
    private Task task;

    private Date date;

    public Hop() {
        this.date = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
*/
