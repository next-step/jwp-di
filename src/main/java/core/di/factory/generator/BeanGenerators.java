package core.di.factory.generator;

import core.di.exception.BeanCreateException;
import core.di.factory.BeanInitInfo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BeanGenerators {
    private final Set<BeanGenerator> generators;

    public BeanGenerators(Collection<BeanGenerator> generators) {
        this.generators = new HashSet<>(generators);
    }

    public Object generate(Set<Class<?>> dependency, Map<Class<?>, Object> beans, BeanInitInfo beanInitInfo) {
        return generators.stream()
                .filter(generator -> generator.support(beanInitInfo))
                .findFirst()
                .map(generator -> generator.generate(dependency, beans, beanInitInfo))
                .orElseThrow(() -> new BeanCreateException("Fail to create bean"));
    }
}
