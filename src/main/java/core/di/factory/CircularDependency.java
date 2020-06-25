package core.di.factory;

import java.util.Collection;
import java.util.stream.Collectors;

public class CircularDependency extends RuntimeException {
    public CircularDependency(Collection<Class<?>> classes) {
        super("There is circular dependency : " +
                classes.stream()
                        .map(Class::getName)
                        .collect(Collectors.joining(" -> "))
        );
    }
}
