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

    @ManyToOne(fetch = FetchType.LAZY)
    private ComponentCategory componentCategory;

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ComponentCategory getComponentCategory() {
        return componentCategory;
    }

    public void setComponentCategory(ComponentCategory componentCategory) {
        this.componentCategory = componentCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Component component = (Component) o;

        if (id != null ? !id.equals(component.id) : component.id != null) return false;
        if (name != null ? !name.equals(component.name) : component.name != null) return false;
        if (description != null ? !description.equals(component.description) : component.description != null)
            return false;
        if (shortName != null ? !shortName.equals(component.shortName) : component.shortName != null) return false;
        if (componentProperties != null ? !componentProperties.equals(component.componentProperties) : component.componentProperties != null)
            return false;
        return date != null ? date.equals(component.date) : component.date == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (shortName != null ? shortName.hashCode() : 0);
        result = 31 * result + (componentProperties != null ? componentProperties.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
