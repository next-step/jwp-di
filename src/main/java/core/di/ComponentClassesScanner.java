package core.di;

import core.annotation.ComponentScan;
import core.util.ReflectionUtils;
import java.util.Arrays;
import java.util.Set;
import org.reflections.Reflections;

public class ComponentClassesScanner {

    private ComponentClassesScanner() {
        throw new AssertionError();
    }

    public static Object[] scanBasePackages(final String targetPackages) {

        final Set<Class<?>> typesAnnotatedWith = ReflectionUtils.getTypesAnnotatedWith(new Reflections(targetPackages), ComponentScan.class);

        return typesAnnotatedWith.stream()
            .map(type -> type.getAnnotation(ComponentScan.class).basePackages())
            .flatMap(Arrays::stream)
            .distinct()
            .toArray();
    }

}
