package next;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@HandlesTypes(AppInitializer.class)
public class AppServletContainerInitializer implements ServletContainerInitializer {
    private static final Logger logger = LoggerFactory.getLogger(AppServletContainerInitializer.class);

    @Override
    public void onStartup(Set<Class<?>> initializerClasses, ServletContext ctx) throws ServletException {
        logger.debug("onStartup {}", this.getClass().getName());

        List<AppInitializer> initializers = new ArrayList<>();

        if (initializerClasses != null) {
            for (Class<?> clazz : initializerClasses) {
                try {
                    initializers.add((AppInitializer) clazz.newInstance());
                } catch (Throwable ex) {
                    throw new ServletException("AppServletContainerInitializer Exception : {}", ex);
                }
            }
        }

        if (initializers.isEmpty()) {
            return;
        }

        for (AppInitializer initializer : initializers) {
            initializer.onStartup(ctx);
        }

    }


}
