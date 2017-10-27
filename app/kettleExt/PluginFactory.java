package kettleExt;

import org.pentaho.di.trans.step.StepMetaInterface;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class PluginFactory {
    public static Object getBean(String beanId) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> c = Class.forName("kettleExt.trans.steps."+beanId);
        Constructor<?> cons = c.getConstructor();
        Object object = cons.newInstance();
        return object;
    }   
}
