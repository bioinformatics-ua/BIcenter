/*package models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
public class Task implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Id
    private String name;

    private String description;
    private Date date;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "task", cascade = CascadeType.ALL)
    private List<Hop> hops;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "task", cascade = CascadeType.ALL)
    private List<Hop> steps;

    public Task(){ date = new Date(); }
    public Task(String name) { this.name = name; }
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }

    public List<Hop> getHops() { return hops; }

    public void setHops(List<Hop> hops) { this.hops = hops; }

    public List<Hop> getSteps() {
        return steps;
    }

    public void setSteps(List<Hop> steps) {
        this.steps = steps;
    }
}
*/