package models;

import javax.persistence.*;

@Entity
public class DataSource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String connectionName;
    private String databaseInterface;
    private int accessType;
    private String hostname;
    private int portNumber;
    private String databaseName;
    private String username;
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    private Institution institution;

    public DataSource() { }

    public DataSource(String connectionName) {
        this.connectionName = connectionName;
    }

    public DataSource(String connectionName, String databaseInterface, int accessType, String hostname, int portNumber, String databaseName, String username, String password) {
        this.connectionName = connectionName;
        this.databaseInterface = databaseInterface;
        this.accessType = accessType;
        this.hostname = hostname;
        this.portNumber = portNumber;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getDatabaseInterface() {
        return databaseInterface;
    }

    public void setDatabaseInterface(String databaseInterface) {
        this.databaseInterface = databaseInterface;
    }

    public int getAccessType() {
        return accessType;
    }

    public void setAccessType(int accessType) {
        this.accessType = accessType;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }
}
