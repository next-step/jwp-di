package core.mvc.tobe.scanner;

import core.annotation.Service;
import org.reflections.Reflections;
import support.exception.ExceptionWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ServiceScanner {

    private final Reflections reflections;

    public ServiceScanner(Object... basePackage) {
        this.reflections = new Reflections(basePackage);
    }

    public Map<Class<?>, Object> getServices() {
        final Set<Class<?>> services = reflections.getTypesAnnotatedWith(Service.class);
        return instantiateServices(services);
    }

    private Map<Class<?>, Object> instantiateServices(Set<Class<?>> preInitiatedServices) {
        final Map<Class<?>, Object> services = new HashMap<>();

        preInitiatedServices.forEach(
                ExceptionWrapper.consumer(service -> services.put(service, service.newInstance())));

        return services;
    }
}
