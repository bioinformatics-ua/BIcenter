package controllers.rbac;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.Inject;
import controllers.login.Secured;
import controllers.rbac.annotation.CheckPermission;
import models.rbac.Category;
import models.rbac.Operation;
import models.rbac.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import repositories.user.UserRepository;
import serializers.user.UserNoChildSerializer;

import java.util.List;


public class Users extends Controller {
    private final UserRepository userRepository;

    @Inject
    public Users(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Security.Authenticated(Secured.class)
    public Result getLoggedInUser() {
        String email = session("userEmail");
        User user = this.userRepository.findByEmail(email);

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(User.class, new UserNoChildSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(user));
    }

    @Security.Authenticated(Secured.class)
    @CheckPermission(category = Category.INSTITUTION, needs = {Operation.GET})
    public Result getAllUsers() {
        List<User> user = this.userRepository.findAll();

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(User.class, new UserNoChildSerializer());
        mapper.registerModule(module);
        Json.setObjectMapper(mapper);

        return ok(Json.toJson(user));
    }
}
