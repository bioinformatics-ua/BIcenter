package models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class ComponentProperty implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String shortName;
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    private Component component;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "componentProperty", cascade = CascadeType.ALL)
    private List<ComponentMetadata> componentMetadatas;

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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public List<ComponentMetadata> getComponentMetadatas() {
        return componentMetadatas;
    }

    public void setComponentMetadatas(List<ComponentMetadata> componentMetadatas) {
        this.componentMetadatas = componentMetadatas;
    }
}