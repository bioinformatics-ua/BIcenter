package models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Server implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String hostName;
    private int portNumber;
    private String username;
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    private Institution institution;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "server", cascade = CascadeType.ALL)
    private List<Schedule> schedules;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<Execution> executions;

    public Server() { };
    public Server(String name){
        this.name = name;
    }
    public Server(String name, String hostName, int portNumber) {
        this.name = name;
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getHostName() { return hostName; }

    public void setHostName(String hostName) { this.hostName = hostName; }

    public int getPortNumber() { return portNumber; }

    public void setPortNumber(int portNumber) { this.portNumber = portNumber; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Institution getInstitution() { return institution; }

    public void setInstitution(Institution institution) { this.institution = institution; }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public List<Execution> getExecutions() {
        return executions;
    }

    public void setExecutions(List<Execution> executions) {
        this.executions = executions;
    }
}
