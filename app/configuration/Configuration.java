package configuration;

import models.Component;

import java.util.List;

public class Configuration {
    private List<Component> components;
    private List<String> operations;
    private List<String> categories;
    private List<CRole> roles;
    private List<CUser> users;
    private List<CAuthentication> authentications;

    public Configuration() {
    }

    public Configuration(List<Component> components, List<String> operations, List<String> categories,
                         List<CRole> roles, List<CUser> users, List<CAuthentication> authentications) {
        this.components = components;
        this.operations = operations;
        this.categories = categories;
        this.roles = roles;
        this.users = users;
        this.authentications = authentications;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    public List<String> getOperations() {
        return operations;
    }

    public void setOperations(List<String> operations) {
        this.operations = operations;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<CRole> getRoles() {
        return roles;
    }

    public void setRoles(List<CRole> roles) {
        this.roles = roles;
    }

    public List<CUser> getUsers() {
        return users;
    }

    public void setUsers(List<CUser> users) {
        this.users = users;
    }

    public List<CAuthentication> getAuthentications() {
        return authentications;
    }

    public void setAuthentications(List<CAuthentication> authentications) {
        this.authentications = authentications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Configuration that = (Configuration) o;

        return components != null ? components.equals(that.components) : that.components == null;
    }

    @Override
    public int hashCode() {
        return components != null ? components.hashCode() : 0;
    }
}
