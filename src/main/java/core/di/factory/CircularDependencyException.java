package core.di.factory;

import java.util.Collection;
import java.util.stream.Collectors;

public class CircularDependencyException extends RuntimeException {
    public CircularDependencyException(Collection<Class<?>> classes) {
        super("There is circular dependency : " +
                classes.stream()
                        .map(Class::getName)
                        .collect(Collectors.joining(" -> "))
        );
    }
}
