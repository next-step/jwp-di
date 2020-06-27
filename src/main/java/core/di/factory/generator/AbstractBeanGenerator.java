package core.di.factory.generator;

import core.di.factory.BeanFactory;
import core.di.factory.BeanInitInfo;

import java.util.Arrays;
import java.util.Set;

public abstract class AbstractBeanGenerator implements BeanGenerator {

    @Override
    public Object generate(Set<Class<?>> dependency, BeanFactory beanFactory, BeanInitInfo beanInitInfo) {
        dependency.add(beanInitInfo.getClassType());

        Object bean = generateBean(dependency, beanFactory, beanInitInfo);

        dependency.remove(beanInitInfo.getClassType());
        return bean;
    }

    abstract Object generateBean(Set<Class<?>> dependency, BeanFactory beanFactory, BeanInitInfo beanInitInfo);

    protected Object[] getArguments(Set<Class<?>> dependency, BeanFactory beanFactory, Class<?>[] parameterTypes) {
        return Arrays.stream(parameterTypes)
                .map(parameterType -> beanFactory.createBean(dependency, parameterType))
                .toArray();
    }
}
