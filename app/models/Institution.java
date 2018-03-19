package models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Institution implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "institution", cascade = CascadeType.ALL)
    private List<Task> tasks;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "institution", cascade = CascadeType.ALL)
    private List<Server> servers;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "institution", cascade = CascadeType.ALL)
    private List<DataSource> dataSources;

    public Institution() { }
    public Institution(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> task) {
        this.tasks = task;
    }

    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> server) {
        this.servers = server;
    }

    public List<DataSource> getDataSources() {
        return dataSources;
    }

    public void setDataSources(List<DataSource> dataSources) {
        this.dataSources = dataSources;
    }
}
