package controllers;

import jsmessages.JsMessages;
import jsmessages.JsMessagesFactory;
import jsmessages.japi.Helper;
import play.i18n.Lang;
import play.i18n.Langs;
import play.libs.Scala;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.routing.JavaScriptReverseRouter;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class Application extends Controller {
    private final Langs langs;
    private final JsMessages jsMessages;

    @Inject
    public Application(Langs langs, JsMessagesFactory jsMessagesFactory) {
        this.langs = langs;
        this.jsMessages = jsMessagesFactory.all();
    }

    public Result jsMessages() {
        return ok(jsMessages.apply(Scala.Option("window.Messages"), Helper.messagesFromCurrentHttpContext()));
    }

    public Result supportedLanguages() {
        List<Lang> langs = request().acceptLanguages();
        String codes = langs.stream().map(Lang::code).collect(Collectors.joining(","));
        return ok(codes);
    }

    public Result javascriptRoutes() {
        response().setHeader(Http.HeaderNames.CONTENT_TYPE, "text/javascript");
        return ok(JavaScriptReverseRouter.create("jsRoutes",
                routes.javascript.Application.supportedLanguages(),
                routes.javascript.TransGraphController.new_task(),
                routes.javascript.TransGraphController.get_task(),
                routes.javascript.TransGraphController.add_step(),
                routes.javascript.TransGraphController.get_steps(),
                routes.javascript.TransGraphController.add_hop(),
                routes.javascript.TransGraphController.select_task(),
                routes.javascript.TransGraphController.load_task(),
                routes.javascript.TransGraphController.preview_results(),
                routes.javascript.StepController.configure(),
                routes.javascript.StepController.getSchema(),
                routes.javascript.StepController.getStep(),
                routes.javascript.StepController.apply_changes()
        ));
//        return ok(
//                Routes.javascriptRouter("jsRoutes",
//                        controllers.routes.javascript.Application.supportedLanguages()
//                )
//        );
    }
}
