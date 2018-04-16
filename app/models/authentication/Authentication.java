package models.authentication;

import models.Institution;
import models.rbac.Role;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Authentication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Constraints.Required
    @Column(nullable = false)
    private AuthenticationType type;

    @Column(nullable = false)
    @Constraints.Required
    private String hostname;

    @Column(nullable = true)
    private String port;

    @Column(nullable = false)
    @Constraints.Required
    private String baseDN;

    @Column(nullable = false)
    @Constraints.Required
    private String usernameAttributes;

    @Column(nullable = false)
    @Constraints.Required
    private String realnameAttributes;

    private String domain;
    private String defaultUser;
    private byte[] defaultPassword;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Role> roles;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Institution> institutions;

    @Constraints.Required
    @Column(nullable = false)
    private Boolean active;

    public Authentication() {
        this.roles = new ArrayList<>();
        this.institutions = new ArrayList<>();
        this.active = true;
    }

    public Authentication(AuthenticationType type, String hostname, String port, String baseDN,
                          String usernameAttributes, String realnameAttributes) {
        this();
        this.type = type;
        this.hostname = hostname;
        this.port = port;
        this.baseDN = baseDN;
        this.usernameAttributes = usernameAttributes;
        this.realnameAttributes = realnameAttributes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuthenticationType getType() {
        return type;
    }

    public void setType(AuthenticationType type) {
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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public byte[] getDefaultPassword() {
        return defaultPassword;
    }

    public void setDefaultPassword(byte[] defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    public List<Institution> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(List<Institution> institutions) {
        this.institutions = institutions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Authentication that = (Authentication) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (type != that.type) return false;
        if (hostname != null ? !hostname.equals(that.hostname) : that.hostname != null) return false;
        if (port != null ? !port.equals(that.port) : that.port != null) return false;
        if (baseDN != null ? !baseDN.equals(that.baseDN) : that.baseDN != null) return false;
        if (usernameAttributes != null ? !usernameAttributes.equals(that.usernameAttributes) : that.usernameAttributes != null)
            return false;
        return realnameAttributes != null ? realnameAttributes.equals(that.realnameAttributes) : that.realnameAttributes == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (hostname != null ? hostname.hashCode() : 0);
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (baseDN != null ? baseDN.hashCode() : 0);
        result = 31 * result + (usernameAttributes != null ? usernameAttributes.hashCode() : 0);
        result = 31 * result + (realnameAttributes != null ? realnameAttributes.hashCode() : 0);
        return result;
    }

    public enum AuthenticationType {
        LDAP,
        AD
    }
}
