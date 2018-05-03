package controllers;

import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import play.mvc.Controller;
import play.mvc.Result;
import utils.StepImageManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Controller that manages all SVGs.
 */
public class SvgController extends Controller {
    public Result getImage(String size, String image) {
        return service(size, image);
    }

    /**
     * Given a certain stepName, it returns the correspondent PNG image.
     *
     * @param stepId stepName.svg.
     * @return PNG file.
     */
    private Result service(String size, String image) {
        File outputfile;
        String stepId = image.replace(".svg", "");

        try {
            PluginRegistry registry = PluginRegistry.getInstance();
            PluginInterface plugin = registry.getPlugin(StepPluginType.class, stepId);
            ClassLoader classLoader = registry.getClassLoader(plugin);
            BufferedImage b_image = StepImageManager.getUniversalImage(classLoader, plugin.getImageFile(), size);
            outputfile = File.createTempFile(stepId, ".png");
            ImageIO.write(b_image, "PNG", outputfile);
        } catch (Exception e) {
            return notFound();
        }
        return ok().sendFile(outputfile);
    }

}
