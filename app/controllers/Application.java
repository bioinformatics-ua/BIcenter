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
                routes.javascript.Assets.versioned(),
                routes.javascript.Application.supportedLanguages(),
                routes.javascript.HomeController.index(),

                // Login controller
                controllers.login.routes.javascript.Login.index(),
                controllers.login.routes.javascript.Login.login(),
                controllers.login.routes.javascript.Login.logout(),
                controllers.rbac.routes.javascript.Users.getLoggedInUser(),

//                routes.javascript.SvgController.getMiddleImage(),
//                routes.javascript.SvgController.getSmallImage(),
                routes.javascript.SvgController.getImage(),

                routes.javascript.InstitutionController.getInstitutionName(),
                routes.javascript.InstitutionController.scheduler(),
                routes.javascript.InstitutionController.getInstitutions(),
                routes.javascript.InstitutionController.newServer(),
                routes.javascript.InstitutionController.deleteServer(),
                routes.javascript.InstitutionController.getServer(),
                routes.javascript.InstitutionController.updateServer(),
                routes.javascript.InstitutionController.newDataSource(),
                routes.javascript.InstitutionController.deleteDataSource(),
                routes.javascript.InstitutionController.getDataSource(),
                routes.javascript.InstitutionController.updateDataSource(),
                routes.javascript.InstitutionController.getDataSources(),
                routes.javascript.InstitutionController.getComponents(),
                routes.javascript.InstitutionController.getSchedules(),

                routes.javascript.TransGraphController.getTaskDetails(),
                routes.javascript.TransGraphController.newTask(),
                routes.javascript.TransGraphController.deleteTask(),
                routes.javascript.TransGraphController.updateTask(),
                routes.javascript.TransGraphController.getTask(),
                routes.javascript.TransGraphController.addStep(),
                routes.javascript.TransGraphController.updateStep(),
                routes.javascript.TransGraphController.removeStep(),
                routes.javascript.TransGraphController.addHop(),
                routes.javascript.TransGraphController.removeHop(),
                routes.javascript.TransGraphController.selectTask(),
                routes.javascript.TransGraphController.loadTask(),
                routes.javascript.TransGraphController.history(),
                routes.javascript.TransGraphController.getExecutions(),
                routes.javascript.TransGraphController.getServers(),

                routes.javascript.StepController.configure(),
                routes.javascript.StepController.showStepInput(),
                routes.javascript.StepController.showStepOutput(),
                routes.javascript.StepController.inputOutputFields(),
                routes.javascript.StepController.inputStepsName(),
                routes.javascript.StepController.inputFieldsName(),
                routes.javascript.StepController.outputStepsName(),
                routes.javascript.StepController.getSchema(),
                routes.javascript.StepController.getStepName(),
                routes.javascript.StepController.getStep(),
                routes.javascript.StepController.applyChanges(),
                routes.javascript.StepController.uploadFile(),
                routes.javascript.StepController.getTables(),
                routes.javascript.StepController.getTableValue(),
                routes.javascript.StepController.getConditions(),
                routes.javascript.StepController.getConditionValue(),
                routes.javascript.StepController.getMultiSelects(),
                routes.javascript.StepController.getInstitution(),

                routes.javascript.StepController.getByComponentAndShortName(),
                routes.javascript.StepController.getMetadataByComponentAndShortName(),

                routes.javascript.ExecutionController.logs(),
                routes.javascript.ExecutionController.metrics(),
                routes.javascript.ExecutionController.previewData(),
                routes.javascript.ExecutionController.previewStep(),

                routes.javascript.ExecutionController.localExecution(),
                routes.javascript.ExecutionController.remoteExecution(),
                routes.javascript.ExecutionController.result(),
                routes.javascript.ExecutionController.getTask(),
                routes.javascript.ExecutionController.getLogs(),
                routes.javascript.ExecutionController.getMetrics(),
                routes.javascript.ExecutionController.getData(),
                routes.javascript.ExecutionController.getStepData(),
                routes.javascript.ExecutionController.deleteSchedule()
        ));
    }
}
