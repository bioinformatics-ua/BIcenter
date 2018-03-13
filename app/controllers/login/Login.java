package controllers.login;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import models.authentication.Authentication;
import models.rbac.User;
import play.Logger;
import play.i18n.Messages;
import play.mvc.*;
import services.authentication.AuthenticationService;
import services.rbac.UserService;

import java.util.List;

public class Login extends Controller {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Inject
    public Login(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    public static void clearSession() {
        // Discard cookies
        response().discardCookie("PLAY_SESSION");

        // Clear session
        session().clear();
    }

    public Result index() {
        return ok(views.html.login.render());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result login() {
        // Get messages
        Messages messages = Http.Context.current().messages();

        // Check JSON
        JsonNode json = request().body().asJson();
        if (json == null) {
            String message = messages.at("badRequest.requiredDataMissing");
            return badRequest(message);
//            return badRequest();
        }

        // Get authentication fields
        String username = json.findPath("username").asText();
        String password = json.findPath("password").asText();

        // Validate fields
        if (username.isEmpty() || password.isEmpty()) {
            return forbidden(messages.at("login.messages.invalid"));
        }

        // Check if user is active
        User user = userService.findByEmail(username);
        if (user != null && (user.getActive() == null || user.getActive() == false)) {
            return forbidden(messages.at("login.messages.invalid"));
        }

        // Perform database login
        if (user != null && user.getType() == User.UserType.Local && userService.authenticate(username, password) != null) {
            setSession(user.getEmail());
            return ok();
        }

        // Perform login on alternative authentication methods
        List<Authentication> authentications = authenticationService.findAll();
        for (Authentication authentication : authentications) {
            if (authentication.getActive() == false) {
                continue;
            }

            try {
                if ((user = authenticationService.authenticate(authentication, username, password)) != null) {
                    // Successful login
                    setSession(user.getEmail());
                    return ok();
                }
            } catch (Exception exp) {
                Logger.error("Error in LDAP/AD", exp);
            }
        }

        // Failed login
        return forbidden(messages.at("login.messages.invalid"));
    }

    @Security.Authenticated(Secured.class)
    public Result logout() {
        // Clear session
        clearSession();
        return redirect(controllers.login.routes.Login.login());
    }

    public void setSession(final String email) {
        // Clear session
//        clearSession();

        // Set session
        User user = userService.findByEmail(email);
        String name = user.getName();

        // User
        session("userEmail", email);
        session("userName", name);

        // Permissions
//        session("permissions", Json.toJson(Users.getPermissions(user)).toString());
    }
}
