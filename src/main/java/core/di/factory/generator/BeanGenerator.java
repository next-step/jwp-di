package core.di.factory.generator;

import core.di.factory.BeanInitInfo;

import java.util.Map;
import java.util.Set;

public interface BeanGenerator {
    boolean support(BeanInitInfo beanInitInfo);
    Object generate(Set<Class<?>> dependency, Map<Class<?>, Object> beans, BeanInitInfo beanInitInfo);
}
