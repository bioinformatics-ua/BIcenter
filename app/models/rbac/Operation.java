package models.rbac;

import play.data.validation.Constraints;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Operation implements Serializable {
    @Transient
    public static final String ADD = "Add";
    @Transient
    public static final String UPDATE = "Update";
    @Transient
    public static final String DELETE = "Delete";
    @Transient
    public static final String GET = "Get";
    @Transient
    public static final String LIST = "List";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Constraints.Required
    @Column(nullable = false)
    private Date created;

    @Constraints.Required
    @Column(nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "operation")
    private Set<Permission> permissions;

    public Operation() {
        this.created = new Date();
        this.permissions = new HashSet<>();
    }

    public Operation(String name) {
        this();
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Operation)) return false;

        Operation operation = (Operation) o;

        if (!name.equals(operation.name)) return false;

        return true;
    }

    @Override
    public String toString() {
        return "Operation[ id=" + id + " ]";
    }
}
