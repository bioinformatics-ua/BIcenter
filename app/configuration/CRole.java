package configuration;

import java.util.List;

/**
 * Created by david on 02/07/15.
 */
public class CRole {
    private String name;
    private List<CPermission> permissions;

    public CRole() {
    }

    public CRole(final String name, final List<CPermission> permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<CPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(final List<CPermission> permissions) {
        this.permissions = permissions;
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final CRole cRole = (CRole) o;

        if (name != null ? !name.equals(cRole.name) : cRole.name != null) return false;
        return !(permissions != null ? !permissions.equals(cRole.permissions) : cRole.permissions != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (permissions != null ? permissions.hashCode() : 0);
        return result;
    }
}
