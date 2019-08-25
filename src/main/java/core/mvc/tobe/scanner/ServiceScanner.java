package core.mvc.tobe.scanner;

import com.google.common.collect.Maps;
import core.annotation.Service;
import org.reflections.Reflections;
import org.springframework.util.CollectionUtils;
import support.exception.ExceptionWrapper;

import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

public class ServiceScanner implements BeanScanner {

    private final Reflections reflections;

    public ServiceScanner(Object... basePackage) {
        this.reflections = new Reflections(basePackage);
    }

    public Map<Class<?>, Object> getBeans() {
        final Set<Class<?>> services = reflections.getTypesAnnotatedWith(Service.class);

        if (CollectionUtils.isEmpty(services)) {
            return Maps.newHashMap();
        }

        return instantiate(services);
    }

    private Map<Class<?>, Object> instantiate(Set<Class<?>> preInitiatedServices) {
        return preInitiatedServices.stream()
                .collect(toMap(
                        service -> service,
                        ExceptionWrapper.function(Class::newInstance)));
    }
}
