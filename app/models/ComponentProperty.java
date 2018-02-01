package models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ComponentProperty implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String shortName;
    private String type;
    private Component component;

    public ComponentProperty() {
    }

    public ComponentProperty(String name, String shortName, String type) {
        this.name = name;
        this.shortName = shortName;
        this.type = type;
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

    public String getShortName() { return shortName; }

    public void setShortName(String shortName) { this.shortName = shortName; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    @ManyToOne
    @JoinColumn(name = "component_id")
    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component){ this.component = component; }
}
