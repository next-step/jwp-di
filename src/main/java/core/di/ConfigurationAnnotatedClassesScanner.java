package core.di;

import core.annotation.Configuration;
import core.util.ReflectionUtils;
import java.util.Set;
import org.reflections.Reflections;

public class ConfigurationAnnotatedClassesScanner {

    private ConfigurationAnnotatedClassesScanner() {
        throw new AssertionError();
    }

    public static Set<Class<?>> scan(final String targetPackages) {
        return ReflectionUtils.getTypesAnnotatedWith(new Reflections(targetPackages), Configuration.class);
    }
}
