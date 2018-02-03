package models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ComponentMetadata implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private ComponentProperty componentProperty;

    public ComponentMetadata() {
    }

    public ComponentMetadata(String name) {
        this.name = name;
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

    public ComponentProperty getComponentProperty() {
        return componentProperty;
    }

    public void setComponentProperty(ComponentProperty componentProperty) {
        this.componentProperty = componentProperty;
    }
}
