package models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ComponentMetadata implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String shortName;
    private String value;
    private String source;

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

    public String getShortName() { return shortName; }

    public void setShortName(String shortName) { this.shortName = shortName; }

    public String getValue() { return value; }

    public void setValue(String value) { this.value = value; }

    public ComponentProperty getComponentProperty() {
        return componentProperty;
    }

    public void setComponentProperty(ComponentProperty componentProperty) {
        this.componentProperty = componentProperty;
    }

    public String getSource() { return source; }

    public void setSource(String source) { this.source = source; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComponentMetadata that = (ComponentMetadata) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (shortName != null ? !shortName.equals(that.shortName) : that.shortName != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        return componentProperty != null ? componentProperty.equals(that.componentProperty) : that.componentProperty == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (shortName != null ? shortName.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (componentProperty != null ? componentProperty.hashCode() : 0);
        return result;
    }
}
