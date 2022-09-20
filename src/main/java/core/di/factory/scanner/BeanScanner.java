package core.di.factory.scanner;

import core.di.factory.definition.BeanDefinition;

public interface Scanner {

    void doScan(Class<?>... configurations);

    boolean support(BeanDefinition beanDefinition);
}
