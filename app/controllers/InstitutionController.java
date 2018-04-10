package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.Inject;
import controllers.login.Secured;
import controllers.rbac.annotation.CheckPermission;
import models.*;
import models.rbac.Category;
import models.rbac.Operation;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
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

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.DATA_SOURCE, needs = {Operation.DELETE})
    public Result deleteDataSource(long institution, long dataSource) {
        this.dataSourceRepository.delete(dataSource);
        return ok();
    }

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.SERVER, needs = {Operation.DELETE})
    public Result deleteServer(long institution, long serverId) {
        this.serverRepository.delete(serverId);
        return ok();
    }

    /**
     * Execution Scheduler page
     *
     * @param institutionId
     * @return
     */
    public Result scheduler(long institutionId) {
        return ok(views.html.index.render());
    }

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.INSTITUTION, needs = {Operation.GET})
    public Result getInstitutionName(long institutionId){
        Institution institution = institutionRepository.get(institutionId);
        return ok(institution.getName());
    }

    public Result getInstitutions(){
        String email = session().get("userEmail");
        List<Institution> institutions = institutionRepository.list(email);

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

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.SERVER, needs = {Operation.ADD})
    public Result newServer(long institutionId, String serverName){
        if(existsServer(serverName))
            return ok("not found");

        Server server = new Server(serverName);
        Institution institution = institutionRepository.get(institutionId);
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

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.SERVER, needs = {Operation.GET})
    public Result getServer(long institutionId, long serverId) {
        Server server = serverRepository.get(serverId);

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Server.class, new CompleteServerSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(server));
    }

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.SERVER, needs = {Operation.UPDATE})
    public Result updateServer(long institutionId, long serverId){
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

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.DATA_SOURCE, needs = {Operation.ADD})
    public Result newDataSource(long institutionId, String dataSourceName){
        if(existsDataSource(dataSourceName))
            return ok("not found");

        DataSource dataSource = new DataSource(dataSourceName);
        Institution institution = institutionRepository.get(institutionId);
        dataSource.setInstitution(institution);
        dataSource = dataSourceRepository.add(dataSource);

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(DataSource.class, new CompleteDataSourceSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

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

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.DATA_SOURCE, needs = {Operation.GET})
    public Result getDataSource(long institutionId, long dataSourceId) {
        DataSource dataSource = dataSourceRepository.get(dataSourceId);

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(DataSource.class, new CompleteDataSourceSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(dataSource));
    }

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.DATA_SOURCE, needs = {Operation.UPDATE})
    public Result updateDataSource(long institutionId, long dataSourceId){
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

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.DATA_SOURCE, needs = {Operation.GET})
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

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.INSTITUTION, needs = {Operation.GET})
    public Result getSchedules(long institutionId){
        Institution institution = institutionRepository.get(institutionId);
        List<Schedule> schedules = institution.getSchedules();

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Schedule.class, new CompleteScheduleSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(schedules));
    }
}
