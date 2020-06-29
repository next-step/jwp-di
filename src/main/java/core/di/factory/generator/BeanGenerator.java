package core.di.factory.generator;

import core.di.factory.BeanFactory;
import core.di.factory.BeanInitInfo;

import java.util.Set;

public interface BeanGenerator {
    boolean support(BeanInitInfo beanInitInfo);
    Object generate(Set<Class<?>> dependency, BeanFactory beanFactory, BeanInitInfo beanInitInfo);
}
