package kettleExt;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Factory that handles transformation's steps.
 */
public class PluginFactory {
    /**
     * Given a certain step name, returns the corresponding step parser.
     * @param beanId Step name.
     * @return Step Parser.
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public static Object getBean(String beanId) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> c = Class.forName("kettleExt.trans.steps."+beanId);
        Constructor<?> cons = c.getConstructor();
        Object object = cons.newInstance();
        return object;
    }   
}
