package controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import kettleExt.utils.SvgImageUrl;

import utils.StepImageManager;

import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * Controller that manages all SVGs.
 */
public class SvgController extends Controller {
    /**
     * Given a certain stepName, it returns the correspondent PNG image.
     * @param image stepName.svg.
     * @return PNG file.
     * @throws IOException
     */
    public Result service(String image) throws IOException {
        String url = request().path();
        String stepId = SvgImageUrl.getStepId(url);

        File outputfile = null;
        try {
            PluginRegistry registry = PluginRegistry.getInstance();
            PluginInterface plugin = registry.getPlugin( StepPluginType.class, stepId);
            ClassLoader classLoader = registry.getClassLoader(plugin);
            BufferedImage b_image = StepImageManager.getUniversalImage(classLoader, plugin.getImageFile(), SvgImageUrl.getSize(url));
            outputfile = File.createTempFile(stepId, ".png");
            ImageIO.write(b_image, "PNG", outputfile);
        } catch(Exception e) {
            return notFound();
        }
        return ok(outputfile).as("image/png");
    }

}
