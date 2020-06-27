package core.di.factory.generator;

import core.di.factory.BeanInitInfo;
import core.di.factory.BeanType;

import java.util.Map;
import java.util.Set;

public class ComponentTypeGenerator implements BeanGenerator {
    @Override
    public boolean support(BeanInitInfo beanInitInfo) {
        BeanType beanType = beanInitInfo.getBeanType();

        return beanType.isComponentType() || beanType == BeanType.CONFIGURATION;
    }

    @Override
    public Object generate(Set<Class<?>> dependency, Map<Class<?>, Object> beans, BeanInitInfo beanInitInfo) {
        return null;
    }
}
