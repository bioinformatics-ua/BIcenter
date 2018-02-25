package models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Entity
public class ComponentProperty implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String shortName;
    private String type;
    private String source;

    @ManyToOne(fetch = FetchType.LAZY)
    private Component component;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "componentProperty", cascade = CascadeType.ALL)
    private List<StepProperty> stepProperties;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "componentProperty", cascade = CascadeType.ALL)
    private List<ComponentMetadata> componentMetadatas;

    public ComponentProperty() {
    }

    public ComponentProperty(String name, String shortName, String type, String source) {
        this.name = name;
        this.shortName = shortName;
        this.type = type;
        this.source = source;
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

    public List<StepProperty> getStepProperties() { return stepProperties; }

    public void setStepProperties(List<StepProperty> stepProperties) { this.stepProperties = stepProperties; }

    public List<ComponentMetadata> getComponentMetadatas() {
        return componentMetadatas;
    }

    public void setComponentMetadatas(List<ComponentMetadata> componentMetadatas) { this.componentMetadatas = componentMetadatas; }

    public String getSource() { return source; }

    public void setSource(String source) { this.source = source; }

    public StepProperty getStepProperty(long stepId) {
        Optional<StepProperty> value = stepProperties.stream()
                .filter(prop -> prop.getStep().getId() == stepId)
                .findFirst();

        if(value.isPresent()) return value.get();
        else return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComponentProperty that = (ComponentProperty) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (shortName != null ? !shortName.equals(that.shortName) : that.shortName != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        if (component != null ? !component.equals(that.component) : that.component != null) return false;
        if (stepProperties != null ? !stepProperties.equals(that.stepProperties) : that.stepProperties != null)
            return false;
        return componentMetadatas != null ? componentMetadatas.equals(that.componentMetadatas) : that.componentMetadatas == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (shortName != null ? shortName.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (component != null ? component.hashCode() : 0);
        result = 31 * result + (stepProperties != null ? stepProperties.hashCode() : 0);
        result = 31 * result + (componentMetadatas != null ? componentMetadatas.hashCode() : 0);
        return result;
    }
}
