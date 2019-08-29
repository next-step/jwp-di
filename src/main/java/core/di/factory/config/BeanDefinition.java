package core.di.factory.config;

import java.util.List;

public interface BeanDefinition {

    Class<?> getBeanType();

    List<Class<?>> getArgumentTypes();


    BeanCreator getBeanCreator();
}
