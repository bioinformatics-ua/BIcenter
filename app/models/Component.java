package models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
public class Component implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String shortName;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "component", cascade = CascadeType.ALL)
    private List<ComponentProperty> componentProperties;

    //@OneToMany(fetch = FetchType.LAZY, mappedBy = "component", cascade = CascadeType.ALL)
    //private List<Step> steps;

    private Date date;

    public Component() {
        date = new Date();
    }

    public Component(String name, String description, String shortName) {
        this();
        this.name = name;
        this.description = description;
        this.shortName = shortName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public List<ComponentProperty> getComponentProperties() {
        return componentProperties;
    }

    public void setComponentProperties(List<ComponentProperty> componentProperties) {
        this.componentProperties = componentProperties;
    }

    /*
    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
    */

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
