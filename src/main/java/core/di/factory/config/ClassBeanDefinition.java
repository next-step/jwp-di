package core.di.factory.config;

import core.di.factory.BeanFactoryUtils;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

public class ClassBeanDefinition implements BeanDefinition {

    private final Class<?> clazz;
    private final Constructor<?> constructor;
    private final List<Class<?>> argumentTypes;

    public ClassBeanDefinition(Class<?> clazz){
        this.clazz = clazz;
        this.constructor = BeanFactoryUtils.getConstructor(clazz);
        this.argumentTypes = Arrays.asList(this.constructor.getParameterTypes());
    }

    @Override
    public Class<?> getBeanType() {
        return this.clazz;
    }

    @Override
    public List<Class<?>> getArgumentTypes() {
        return this.argumentTypes;
    }

    @Override
    public BeanCreator getBeanCreator(){
        return (args) -> {
            return this.constructor.newInstance(args);
        };
    }
}
