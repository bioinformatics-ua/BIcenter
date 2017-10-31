package controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import kettleExt.utils.SvgImageUrl;
import play.mvc.Controller;
import play.mvc.Result;
import utils.StepImageManager;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;

import static play.mvc.Controller.request;
import static play.mvc.Results.notFound;
import static play.mvc.Results.ok;

public class SvgController extends Controller {

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
