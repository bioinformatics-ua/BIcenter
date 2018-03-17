package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.Inject;
import models.Institution;
import models.Server;
import models.Task;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import repositories.InstitutionRepository;
import repositories.ServerRepository;
import serializers.institution.CompleteServerSerializer;
import serializers.institution.InstitutionSerializer;
import serializers.institution.ServerSerializer;
import serializers.task.SimpleTaskSerializer;

import javax.persistence.NoResultException;
import java.util.List;

public class InstitutionController extends Controller {
    private final InstitutionRepository institutionRepository;
    private final ServerRepository serverRepository;

    @Inject
    public InstitutionController(InstitutionRepository institutionRepository, ServerRepository serverRepository) {
        this.institutionRepository = institutionRepository;
        this.serverRepository = serverRepository;
    }

    public Result getInstitutions(){
        List<Institution> institutions = institutionRepository.list();

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Institution.class, new InstitutionSerializer());
        module.addSerializer(Task.class, new SimpleTaskSerializer());
        module.addSerializer(Server.class, new ServerSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(institutions));
    }

    public Result newServer(String instName, String serverName){
        if(existsServer(serverName)) forbidden();

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
}
