package core.di.factory.generator;

import core.di.factory.BeanFactory;
import core.di.factory.BeanInitInfo;
import core.di.factory.BeanType;

import java.util.Set;

public class MethodTypeGenerator implements BeanGenerator {
    @Override
    public boolean support(BeanInitInfo beanInitInfo) {
        return beanInitInfo.getBeanType() == BeanType.BEAN;
    }

    @Override
    public Object generate(Set<Class<?>> dependency, BeanFactory beanFactory, BeanInitInfo beanInitInfo) {
        return null;
    }
}
