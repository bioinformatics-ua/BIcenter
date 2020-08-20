package services;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import configuration.CAuthentication;
import configuration.CUser;
import configuration.Configuration;
import models.Component;
import models.ComponentCategory;
import models.ComponentProperty;
import models.Institution;
import models.authentication.Authentication;
import models.rbac.User;
import org.mindrot.jbcrypt.BCrypt;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import play.inject.ApplicationLifecycle;
import repositories.ComponentCategoryRepository;
import repositories.ComponentRepository;
import repositories.InstitutionRepository;
import repositories.authentication.AuthenticationRepository;
import repositories.user.RBACRepository;
import services.authentication.AuthenticationService;
import services.rbac.UserService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class demonstrates how to run code when the
 * application starts and stops. It starts a timer when the
 * application starts. When the application stops it prints out how
 * long the application was running for.
 * <p>
 * This class is registered for Guice dependency injection in the
 * {@link Module} class. We want the class to start when the application
 * starts, so it is registered as an "eager singleton". See the code
 * in the {@link Module} class to see how this happens.
 * <p>
 * This class needs to run code when the server stops. It uses the
 * application's {@link ApplicationLifecycle} to register a stop hook.
 */
@Singleton
public class ApplicationStart {
    private static final String filePath = "conf" + File.separator + "configuration.json";

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("application");

    private final ComponentCategoryRepository componentCategoryRepository;
    private final ComponentRepository componentRepository;
    private final RBACRepository rbacRepository;
    private final UserService userService;
    private final AuthenticationRepository authenticationRepository;
    private final AuthenticationService authenticationService;
    private final InstitutionRepository institutionRepository;

    @Inject
    public ApplicationStart(ComponentCategoryRepository componentCategoryRepository, ComponentRepository componentRepository, RBACRepository rbacRepository, UserService userService, AuthenticationRepository authenticationRepository, AuthenticationService authenticationService, InstitutionRepository institutionRepository) {
        this.componentCategoryRepository = componentCategoryRepository;
        this.componentRepository = componentRepository;
        this.rbacRepository = rbacRepository;
        this.userService = userService;
        this.authenticationRepository = authenticationRepository;
        this.authenticationService = authenticationService;
        this.institutionRepository = institutionRepository;

        // Get configuration from JSON file
        Gson gson = new Gson();
        Configuration configuration;
        try {
            configuration = gson.fromJson(new FileReader(filePath), Configuration.class);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("It was not possible to find the configurations file.");
        }

        // Building Step Configurations.
        buildComponents(configuration);

        // Building users
        boolean initRBAC = rbacRepository.findAllRoles().size() == 0;
        if (initRBAC) {
            buildRBAC(configuration);
        }

        // Building Institutions.
        boolean initInstitutions = this.institutionRepository.list().size() == 0;
        if (initInstitutions) {
            try {
                buildInstitutions(configuration);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        boolean initAuth = authenticationRepository.findAll().size() == 0;
        if (initAuth) {
            try {
                buildAuth(configuration);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Build Execution Schedulers.


        // Setting up Kettle environment.
        initKettle();
    }

    private void buildInstitutions(Configuration configuration) throws Exception{
        logger.info("Building Institutions.");

        for (Institution inst : configuration.getInstitutions()){
            Institution institution = new Institution(inst.getName());
            institutionRepository.add(institution);
        }
    }

    private void buildAuth(Configuration configuration) throws Exception {
        logger.info("Building Auth.");

        for (CAuthentication auth : configuration.getAuthentications()) {
            Authentication authentication = new Authentication(
                    Authentication.AuthenticationType.valueOf(auth.getType()),
                    auth.getHostname(),
                    auth.getPort(),
                    auth.getBaseDN(),
                    auth.getUsernameAttributes(),
                    auth.getRealnameAttributes()
            );

            authentication.setDomain(auth.getDomain());
            authentication.setDefaultUser(auth.getDefaultUser());
            if (auth.getDefaultPassword() != null) {
                authentication.setDefaultPassword(AuthenticationService.encryptPassword(auth.getHostname(), auth.getDefaultPassword()));
            }

            authentication.setActive(auth.getActive());

            authenticationService.createAuthentication(authentication, auth.getRoles(), auth.getInstitutions());
        }
    }

    private void buildRBAC(Configuration configuration) {
        logger.info("Building RBAC.");

        // Init roles
        rbacRepository.initializeRoles(configuration.getOperations(), configuration.getCategories(), configuration.getRoles());

        for (CUser u : configuration.getUsers()) {
            User user = new User(u.getEmail(), u.getName(), BCrypt.hashpw(u.getPassword(), BCrypt.gensalt()));
            user.setActive(u.getActive());

            userService.createUserWithRoles(user, u.getRoles());
        }
    }

    /**
     * Load components specifications into the database
     */
    private void buildComponents(Configuration configuration) {
        logger.info("Building Step Configurations.");

        // Get existent configurations.
        List<String> components = componentRepository.list()
                .stream()
                .map(Component::getName)
                .collect(Collectors.toList());

        // Load missing configurations.
        configuration.getComponentCategories()
                .stream()
                .forEach(componentCategory -> {
                    ComponentCategory JPAcomponentCategory = componentCategoryRepository.get(componentCategory.getName());
                    componentCategory.getComponents()
                            .stream()
                            .filter(component -> !components.contains(component.getName()))
                            .peek(component -> {
                                component.setComponentCategory(JPAcomponentCategory);
                                List<ComponentProperty> componentProperties = component.getComponentProperties();
                                if (componentProperties != null) {
                                    component.getComponentProperties()
                                            .stream()
                                            .forEach(cp ->
                                            {
                                                cp.setComponent(component);
                                                if (cp.getComponentMetadatas() != null) {
                                                    cp.getComponentMetadatas()
                                                            .stream()
                                                            .forEach(cm ->
                                                            {
                                                                cm.setComponentProperty(cp);
                                                                if (cm.getMetadatas() != null) {
                                                                    cm.getMetadatas()
                                                                            .stream()
                                                                            .forEach(m -> m.setComponentMetadata(cm));
                                                                }
                                                            });
                                                }
                                            });
                                }
                            })
                            .forEach(componentRepository::add);
                });

    }

    /**
     * Setup Kettle environment.
     */
    private void initKettle() {
        logger.info("Setting up Kettle environment.");
        try {
            KettleLogStore.init(5000, 720);
            KettleEnvironment.init(false);
            try {
                Props.init(Props.TYPE_PROPERTIES_SPOON);
            } catch (RuntimeException e) {
                logger.info("The Properties systems settings are already initialised!");
            }
        } catch (KettleException e) {
            e.printStackTrace();
        }
    }

	/**
	 * Build Quartz Schedules based on the JPA Schedules.
	 */
	private void buildSchedulers() {
		// TODO: missing implementation
	}
}
