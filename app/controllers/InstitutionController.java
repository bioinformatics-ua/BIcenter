package controllers;

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
import serializers.institution.InstitutionSerializer;
import serializers.institution.ServerSerializer;
import serializers.task.SimpleTaskSerializer;

import java.util.List;

public class InstitutionController extends Controller {
    private final InstitutionRepository institutionRepository;

    @Inject
    public InstitutionController(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
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
}
