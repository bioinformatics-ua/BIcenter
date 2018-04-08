package controllers.rbac.annotation;

import com.google.inject.Inject;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import repositories.InstitutionRepository;
import services.rbac.PermissionsService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by luis on 21/10/14.
 */
public class CheckPermissionAction extends Action<CheckPermission> {
    private final PermissionsService permissionsService;
    private final InstitutionRepository institutionRepository;

    @Inject
    public CheckPermissionAction(PermissionsService permissionsService, InstitutionRepository institutionRepository) {
        this.permissionsService = permissionsService;
        this.institutionRepository = institutionRepository;
    }

    @Override
    public CompletionStage<Result> call(Http.Context ctx) {
        // Check license
//        License license = License.getInstance();
//        if (license.isExpired() || license.isRevoked()) {
//            ctx.session().clear();
//            return F.Promise.pure(Results.redirect(controllers.license.routes.Licenses.expired()));
//        }
//        if (!license.hasConnectivity()) {
//            ctx.session().clear();
//            return F.Promise.pure(Results.redirect(controllers.license.routes.Licenses.noconnectivity()));
//        }

        // Check username
        String username = ctx.session().get("userEmail");
        if (username == null) {
            ctx.session().clear();
//            return CompletableFuture.completedFuture(Results.redirect(controllers.login.routes.Login.index(null, "denied")));
            return CompletableFuture.completedFuture(Results.redirect(controllers.login.routes.Login.index()));
        }

        // Check permission
        if (!permissionsService.hasPermission(username, configuration.needs(), configuration.category())) {
            ctx.session().clear();
//            return CompletableFuture.completedFuture(Results.redirect(controllers.login.routes.Login.index(null, "denied")));
            return CompletableFuture.completedFuture(Results.redirect(controllers.login.routes.Login.index()));

        }

        Pattern institutionIdPattern = Pattern.compile("^\\/institution\\/(\\d+)\\/");
        Matcher matcher = institutionIdPattern.matcher(ctx.request().path());
        if (matcher.find()) {
            long institutionId = Long.valueOf(matcher.group(1));
            if (!institutionRepository.hasUser(institutionId, username)) {
                ctx.session().clear();
                return CompletableFuture.completedFuture(Results.redirect(controllers.login.routes.Login.index()));
//                return CompletableFuture.completedFuture(Results.forbidden());
            }
        }

        return delegate.call(ctx);
    }


}
