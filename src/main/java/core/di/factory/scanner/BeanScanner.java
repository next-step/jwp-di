package core.di.factory.scanner;

import core.di.factory.definition.BeanDefinition;

public interface BeanScanner {

    void doScan(Class<?>... configurations);
}
