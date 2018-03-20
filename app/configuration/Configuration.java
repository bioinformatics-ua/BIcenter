package configuration;

import models.ComponentCategory;
import models.Institution;

import java.util.List;

public class Configuration {
    private List<ComponentCategory> componentCategories;
    private List<String> operations;
    private List<String> categories;
    private List<CRole> roles;
    private List<CUser> users;
    private List<CAuthentication> authentications;
    private List<Institution> institutions;

    public Configuration() {
    }

    public Configuration(List<ComponentCategory> componentCategories, List<String> operations, List<String> categories,
                         List<CRole> roles, List<CUser> users, List<CAuthentication> authentications, List<Institution> institutions) {
        this.componentCategories = componentCategories;
        this.operations = operations;
        this.categories = categories;
        this.roles = roles;
        this.users = users;
        this.authentications = authentications;
        this.institutions = institutions;
    }

    public List<ComponentCategory> getComponentCategories() {
        return componentCategories;
    }

    public void setComponentCategories(List<ComponentCategory> componentCategories) {
        this.componentCategories = componentCategories;
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

    public List<Institution> getInstitutions() { return institutions; }

    public void setInstitutions(List<Institution> institutions) { this.institutions = institutions; }

}
