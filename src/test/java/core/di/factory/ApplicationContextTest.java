package core.di.factory;

import core.mvc.MyConfiguration;
import core.mvc.tobe.AnnotationHandlerMapping;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class ApplicationContextTest {

    @Test
    public void createAnnotationHandler() {
        Set<Class<?>> configurationClasses = new HashSet<>();
        configurationClasses.add(MyConfiguration.class);

        ApplicationContext applicationContext = new ApplicationContext(configurationClasses);
        AnnotationHandlerMapping annotationHandlerMapping = new AnnotationHandlerMapping(applicationContext);
        annotationHandlerMapping.initialize();

    }
}
