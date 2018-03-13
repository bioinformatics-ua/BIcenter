package models.rbac;

import play.data.validation.Constraints;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Constraints.Required
    @Column(nullable = false)
    private Date created;

    @Column(nullable = false)
    @Constraints.Required
    private String name;

    @ManyToMany(mappedBy = "roles", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Permission> permissions;

    public Role() {
        this.created = new Date(System.currentTimeMillis());
        this.permissions = new ArrayList<>();
    }

    public Role(String name) {
        this();
        this.name = name;
    }

    @Transient
    public boolean hasPermission(String operationName, String categoryName) {
        for (Permission permission : this.permissions) {
            if (operationName.equals(permission.getOperation().getName()) && categoryName.equals(permission.getCategory().getName())) {
                return true;
            }
        }

        return false;
    }

    public void addPermission(Permission per) {
        if (!permissions.contains(per)) {
            permissions.add(per);
        }
        if (!per.getRoles().contains(this)) {
            per.getRoles().add(this);
        }
    }

    public void removePermission(Permission per) {
        if (permissions.contains(per)) {
            permissions.remove(per);
        }
        if (per.getRoles().contains(this)) {
            per.getRoles().remove(this);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;

        Role role = (Role) o;

        if (!name.equals(role.name)) return false;

        return true;
    }

    @Override
    public String toString() {
        return "Role[ id=" + id + " ]";
    }

}
