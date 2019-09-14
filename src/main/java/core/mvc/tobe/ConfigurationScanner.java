package core.mvc.tobe;

import core.annotation.Configuration;
import org.apache.commons.lang3.ArrayUtils;
import org.reflections.Reflections;

import java.util.Set;

public class ConfigurationScanner implements BeanScanner {

    private final Reflections reflections;

    public ConfigurationScanner(Object... basePackages) {
        if (ArrayUtils.isEmpty(basePackages)) {
            this.reflections = new Reflections("");
            return;
        }

        this.reflections = new Reflections(basePackages);
    }

    @Override
    public Set<Class<?>> getTypes() {
        return reflections.getTypesAnnotatedWith(Configuration.class);
    }
}
