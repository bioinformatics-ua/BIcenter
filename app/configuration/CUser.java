package configuration;

import java.util.List;

/**
 * Created by david on 02/07/15.
 */
public class CUser {
    private String name;
    private String email;
    private String password;
    private Boolean active = true;
    private List<String> roles;

    public CUser() {
    }

    public CUser(final String name, final String email, final String password,
                 final Boolean active, final List<String> roles) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.active = active;
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(final Boolean active) {
        this.active = active;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(final List<String> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CUser cUser = (CUser) o;

        if (name != null ? !name.equals(cUser.name) : cUser.name != null) return false;
        if (email != null ? !email.equals(cUser.email) : cUser.email != null) return false;
        if (password != null ? !password.equals(cUser.password) : cUser.password != null) return false;
        if (active != null ? !active.equals(cUser.active) : cUser.active != null) return false;
        return roles != null ? roles.equals(cUser.roles) : cUser.roles == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (active != null ? active.hashCode() : 0);
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        return result;
    }
}
