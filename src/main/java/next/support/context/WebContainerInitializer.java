package next.support.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

@HandlesTypes({ApplicationInitializer.class})
public class WebContainerInitializer implements ServletContainerInitializer {
    private static final Logger logger = LoggerFactory.getLogger(WebContainerInitializer.class);

    @Override
    public void onStartup(Set<Class<?>> initializerClasses, ServletContext ctx) throws ServletException {
        try {
            Iterator<Class<?>> iter = initializerClasses.iterator();
            while (iter.hasNext()) {
                Class<?> clazz = iter.next();
                Method method = clazz.getDeclaredMethod("onStartup", ServletContext.class);
                method.invoke(clazz.newInstance(), ctx);
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error(e.toString());
            throw new RuntimeException(e);
        }
    }
}
