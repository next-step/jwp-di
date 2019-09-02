package core.mvc;

import org.springframework.beans.BeanUtils;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.util.Set;
import java.util.stream.Collectors;

@HandlesTypes(value = WebApplicationInitializer.class)
public class ServletContainerInitializerImpl implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {

        if (classes == null) {
            return;
        }

        Set<WebApplicationInitializerImpl> initializers = classes.stream()
                .map(BeanUtils::instantiateClass)
                .map(instance -> (WebApplicationInitializerImpl) instance)
                .collect(Collectors.toSet());

        initializers.forEach(initializer -> initializer.onStartup(servletContext));
    }
}
