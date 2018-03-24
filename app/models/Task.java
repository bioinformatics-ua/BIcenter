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

    @ManyToOne(fetch = FetchType.LAZY)
    private Institution institution;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taskHops", cascade = CascadeType.ALL)
    private List<Hop> hops;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taskSteps", cascade = CascadeType.ALL)
    private List<Step> steps;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "task", cascade = CascadeType.ALL)
    private List<Execution> executions;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "task", cascade = CascadeType.ALL)
    private Schedule schedule;

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

    public Institution getInstitution() { return institution; }

    public void setInstitution(Institution institution) { this.institution = institution; }

    public List<Hop> getHops() { return hops; }

    public void setHops(List<Hop> hops) { this.hops = hops; }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public List<Execution> getExecutions() { return executions; }

    public void setExecutions(List<Execution> executions) { this.executions = executions; }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (open != task.open) return false;
        if (id != null ? !id.equals(task.id) : task.id != null) return false;
        if (name != null ? !name.equals(task.name) : task.name != null) return false;
        if (description != null ? !description.equals(task.description) : task.description != null) return false;
        if (date != null ? !date.equals(task.date) : task.date != null) return false;
        if (hops != null ? !hops.equals(task.hops) : task.hops != null) return false;
        return steps != null ? steps.equals(task.steps) : task.steps == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + open;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (hops != null ? hops.hashCode() : 0);
        result = 31 * result + (steps != null ? steps.hashCode() : 0);
        return result;
    }
}