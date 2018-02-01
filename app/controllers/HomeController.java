package controllers;

import models.Component;
import models.ComponentMetadata;
import models.ComponentProperty;
import play.mvc.*;

import repositories.ComponentMetadataRepository;
import repositories.ComponentPropertyRepository;
import repositories.ComponentRepository;
import views.html.*;

import javax.inject.Inject;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("application");

    private final ComponentRepository componentRepository;
    private final ComponentPropertyRepository componentPropertyRepository;
    private final ComponentMetadataRepository componentMetadataRepository;

    @Inject
    public HomeController(ComponentRepository componentRepository, ComponentPropertyRepository componentPropertyRepository, ComponentMetadataRepository componentMetadataRepository) {
        this.componentRepository = componentRepository;
        this.componentPropertyRepository = componentPropertyRepository;
        this.componentMetadataRepository = componentMetadataRepository;
    }

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() { return ok(index.render()); }
}
