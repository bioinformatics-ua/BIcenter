package models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
public class Task implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;
    private int open;
    private Date date;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taskHops", cascade = CascadeType.ALL)
    private List<Hop> hops;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taskSteps", cascade = CascadeType.ALL)
    private List<Step> steps;

    public Task(){ date = new Date(); }
    public Task(String name) {
        this.name = name;
        this.open = 1;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public int getOpen() { return open; }

    public void setOpen(int open) { this.open = open; }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }

    public List<Hop> getHops() { return hops; }

    public void setHops(List<Hop> hops) { this.hops = hops; }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
}