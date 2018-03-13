package models.rbac;

import play.data.validation.Constraints;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Permission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Constraints.Required
    @Column(nullable = false)
    private Date created;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Role> roles;

    @ManyToOne
    @JoinColumn(name = "operation_id")
    private Operation operation;

    @Column
    private String permission;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Permission() {
        this.created = new Date();
        this.roles = new HashSet<>();
    }

    public Permission(Operation operation, Category category) {
        this();
        this.operation = operation;
        this.category = category;
    }

    public Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Set<Role> getRoles() {
        if (this.roles == null)
            this.roles = new HashSet<>();
        return this.roles;
    }

    @Override
    public int hashCode() {
        int result = (operation != null ? operation.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Permission that = (Permission) o;

        if (operation != null ? !operation.equals(that.operation) : that.operation != null) return false;
        return category != null ? category.equals(that.category) : that.category == null;
    }

    @Override
    public String toString() {
        return "Permission[ id=" + id + " ]";
    }

}
