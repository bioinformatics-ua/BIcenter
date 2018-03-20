package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.Inject;
import models.*;
import models.rbac.Category;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import repositories.*;
import serializers.component.SimpleComponentSerializer;
import serializers.institution.*;
import serializers.task.SimpleTaskSerializer;

import javax.persistence.NoResultException;
import java.util.List;

public class InstitutionController extends Controller {
    private final InstitutionRepository institutionRepository;
    private final ServerRepository serverRepository;
    private final DataSourceRepository dataSourceRepository;
    private final ComponentRepository componentRepository;
    private final ComponentCategoryRepository componentCategoryRepository;

    @Inject
    public InstitutionController(InstitutionRepository institutionRepository, ServerRepository serverRepository, DataSourceRepository dataSourceRepository, ComponentRepository componentRepository, ComponentCategoryRepository componentCategoryRepository) {
        this.institutionRepository = institutionRepository;
        this.serverRepository = serverRepository;
        this.dataSourceRepository = dataSourceRepository;
        this.componentRepository = componentRepository;
        this.componentCategoryRepository = componentCategoryRepository;
    }

    public Result getInstitutions(){
        List<Institution> institutions = institutionRepository.list();

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Institution.class, new InstitutionSerializer());
        module.addSerializer(Task.class, new SimpleTaskSerializer());
        module.addSerializer(Server.class, new ServerSerializer());
        module.addSerializer(DataSource.class, new DataSourceSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(institutions));
    }

    public Result newServer(String instName, String serverName){
        if(existsServer(serverName))
            return ok("not found");

        Server server = new Server(serverName);
        Institution institution = institutionRepository.getByName(instName);
        server.setInstitution(institution);
        server = serverRepository.add(server);

        return ok(Json.toJson(server));
    }

    public boolean existsServer(String name){
        boolean exists = true;
        if(serverRepository.list().isEmpty()) exists = false;
        else{
            try{
                serverRepository.getByName(name);
            }
            catch(NoResultException e){
                exists = false;
            }
        }
        if(exists) return true;
        return false;
    }

    public Result getServer(long serverId) {
        Server server = serverRepository.get(serverId);

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Server.class, new CompleteServerSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(server));
    }

    public Result updateServer(long serverId){
        JsonNode formData = request().body().asJson();

        Server server = serverRepository.get(serverId);
        Institution institution = institutionRepository.get(formData.get("institution").asInt());
        server.setInstitution(institution);
        server.setName(formData.get("name").asText());
        server.setHostName(formData.get("hostName").asText());
        server.setPortNumber(formData.get("portNumber").asInt());
        server.setHostName(formData.get("hostName").asText());
        server.setUsername(formData.get("username").asText());
        server.setPassword(formData.get("password").asText());
        server = serverRepository.add(server);

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Server.class, new CompleteServerSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(server));
    }

    public Result newDataSource(String instName, String dataSourceName){
        if(existsDataSource(dataSourceName))
            return ok("not found");

        DataSource dataSource = new DataSource(dataSourceName);
        Institution institution = institutionRepository.getByName(instName);
        dataSource.setInstitution(institution);
        dataSource = dataSourceRepository.add(dataSource);

        return ok(Json.toJson(dataSource));
    }

    public boolean existsDataSource(String name){
        boolean exists = true;
        if(dataSourceRepository.list().isEmpty()) exists = false;
        else{
            try{
                dataSourceRepository.getByName(name);
            }
            catch(NoResultException e){
                exists = false;
            }
        }
        if(exists) return true;
        return false;
    }

    public Result getDataSource(long dataSourceId) {
        DataSource dataSource = dataSourceRepository.get(dataSourceId);

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(DataSource.class, new CompleteDataSourceSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(dataSource));
    }

    public Result updateDataSource(long dataSourceId){
        JsonNode formData = request().body().asJson();

        DataSource dataSource = dataSourceRepository.get(dataSourceId);
        Institution institution = institutionRepository.get(formData.get("institution").asInt());

        dataSource.setInstitution(institution);
        dataSource.setConnectionName(formData.get("connectionName").asText());
        dataSource.setDatabaseInterface(formData.get("databaseInterface").asText());
        dataSource.setAccessType(formData.get("accessType").asInt());
        dataSource.setHostname(formData.get("hostName").asText());
        dataSource.setPortNumber(formData.get("portNumber").asInt());
        dataSource.setDatabaseName(formData.get("dBName").asText());
        dataSource.setUsername(formData.get("username").asText());
        dataSource.setPassword(formData.get("password").asText());

        dataSource = dataSourceRepository.add(dataSource);

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(DataSource.class, new CompleteDataSourceSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(dataSource));
    }

    public Result getDataSources(long institutionId) {
        Institution institution = institutionRepository.get(institutionId);
        List<DataSource> dataSources = institution.getDataSources();

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(DataSource.class, new ConnectionSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(dataSources));
    }

    public Result getComponents() {
        List<ComponentCategory> componentCategories = componentCategoryRepository.list();

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Component.class, new SimpleComponentSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(componentCategories));
    }
}
