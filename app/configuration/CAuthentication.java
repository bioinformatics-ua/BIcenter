package configuration;

import java.util.List;

/**
 * Created by david on 02/07/15.
 */
public class CAuthentication {
    private String type;
    private String hostname;
    private String port;
    private String baseDN;
    private String usernameAttributes;
    private String realnameAttributes;
    private String domain;
    private String defaultUser;
    private String defaultPassword;


    private Boolean active = true;
    private List<String> roles;

    public CAuthentication() {
    }

    public CAuthentication(String type, String hostname, String port, String baseDN, String usernameAttributes,
                           String realnameAttributes, String domain, String defaultUser, String defaultPassword,
                           Boolean active, List<String> roles) {
        this.type = type;
        this.hostname = hostname;
        this.port = port;
        this.baseDN = baseDN;
        this.usernameAttributes = usernameAttributes;
        this.realnameAttributes = realnameAttributes;
        this.domain = domain;
        this.defaultUser = defaultUser;
        this.defaultPassword = defaultPassword;
        this.active = active;
        this.roles = roles;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getBaseDN() {
        return baseDN;
    }

    public void setBaseDN(String baseDN) {
        this.baseDN = baseDN;
    }

    public String getUsernameAttributes() {
        return usernameAttributes;
    }

    public void setUsernameAttributes(String usernameAttributes) {
        this.usernameAttributes = usernameAttributes;
    }

    public String getRealnameAttributes() {
        return realnameAttributes;
    }

    public void setRealnameAttributes(String realnameAttributes) {
        this.realnameAttributes = realnameAttributes;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDefaultUser() {
        return defaultUser;
    }

    public void setDefaultUser(String defaultUser) {
        this.defaultUser = defaultUser;
    }

    public String getDefaultPassword() {
        return defaultPassword;
    }

    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CAuthentication that = (CAuthentication) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (hostname != null ? !hostname.equals(that.hostname) : that.hostname != null) return false;
        if (port != null ? !port.equals(that.port) : that.port != null) return false;
        if (baseDN != null ? !baseDN.equals(that.baseDN) : that.baseDN != null) return false;
        if (usernameAttributes != null ? !usernameAttributes.equals(that.usernameAttributes) : that.usernameAttributes != null)
            return false;
        if (realnameAttributes != null ? !realnameAttributes.equals(that.realnameAttributes) : that.realnameAttributes != null)
            return false;
        if (domain != null ? !domain.equals(that.domain) : that.domain != null) return false;
        if (defaultUser != null ? !defaultUser.equals(that.defaultUser) : that.defaultUser != null) return false;
        if (defaultPassword != null ? !defaultPassword.equals(that.defaultPassword) : that.defaultPassword != null)
            return false;
        if (active != null ? !active.equals(that.active) : that.active != null) return false;
        return roles != null ? roles.equals(that.roles) : that.roles == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (hostname != null ? hostname.hashCode() : 0);
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (baseDN != null ? baseDN.hashCode() : 0);
        result = 31 * result + (usernameAttributes != null ? usernameAttributes.hashCode() : 0);
        result = 31 * result + (realnameAttributes != null ? realnameAttributes.hashCode() : 0);
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        result = 31 * result + (defaultUser != null ? defaultUser.hashCode() : 0);
        result = 31 * result + (defaultPassword != null ? defaultPassword.hashCode() : 0);
        result = 31 * result + (active != null ? active.hashCode() : 0);
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        return result;
    }
}
