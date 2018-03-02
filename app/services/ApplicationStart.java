package services;

import com.google.gson.Gson;
import configuration.Configuration;
import models.Component;
import models.ComponentProperty;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import play.inject.ApplicationLifecycle;
import repositories.ComponentRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
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
    private static String filePath = "conf" + File.separator + "configuration.json";

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("application");

    private final ComponentRepository componentRepository;

    @Inject
    public ApplicationStart(ComponentRepository componentRepository) {
        this.componentRepository = componentRepository;

        // Building Step Configurations.
        buildComponents();

        // Setting up Kettle environment.
        initKettle();
    }

    /**
     * Load components specifications into the database
     */
    private void buildComponents(){
        logger.info("Building Step Configurations.");

        // Get configuration from JSON file
        Gson gson = new Gson();
        Configuration configuration;
        try {
            configuration = gson.fromJson(new FileReader(filePath), Configuration.class);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("It was not possible to find the configurations file.");
        }

        // Get existent configurations.
        List<String> components = componentRepository.list()
                .stream()
                .map(Component::getName)
                .collect(Collectors.toList());

        // Load missing configurations.
        configuration.getComponents()
            .stream()
            .filter(component -> !components.contains(component.getName()))
            .peek(component -> {
                List<ComponentProperty> componentProperties = component.getComponentProperties();
                if(componentProperties != null) {
                    component.getComponentProperties()
                            .stream()
                            .forEach(cp ->
                            {
                                cp.setComponent(component);
                                if (cp.getComponentMetadatas() != null)
                                    cp.getComponentMetadatas()
                                            .stream()
                                            .forEach(cm -> cm.setComponentProperty(cp));
                            });
                }
            })
            .forEach(componentRepository::add);
    }

    /**
     * Setup Kettle environment.
     */
    private void initKettle(){
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
}
