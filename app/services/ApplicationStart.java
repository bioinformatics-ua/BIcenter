package services;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import javax.inject.*;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import play.Logger;
import play.inject.ApplicationLifecycle;

/**
 * This class demonstrates how to run code when the
 * application starts and stops. It starts a timer when the
 * application starts. When the application stops it prints out how
 * long the application was running for.
 *
 * This class is registered for Guice dependency injection in the
 * {@link Module} class. We want the class to start when the application
 * starts, so it is registered as an "eager singleton". See the code
 * in the {@link Module} class to see how this happens.
 *
 * This class needs to run code when the server stops. It uses the
 * application's {@link ApplicationLifecycle} to register a stop hook.
 */
@Singleton
public class ApplicationStart {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("application");

    @Inject
    public ApplicationStart() {
        logger.info("Setting up Kettle environment.");
        try {
            KettleLogStore.init( 5000, 720 );
            KettleEnvironment.init();
            try{
                Props.init( Props.TYPE_PROPERTIES_SPOON );
            }catch(RuntimeException e){
                logger.info("The Properties systems settings are already initialised!");
            }
        } catch (KettleException e) {
            e.printStackTrace();
        }
    }

}
