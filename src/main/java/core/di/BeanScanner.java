package core.di;

import java.util.Set;

public interface BeanScanner {
    void scan(Set<Class<?>> configurationClasses);
}
