package core.di.scanner;

import core.di.factory.BeanFactory;

public interface Scanner {
    void scan(BeanFactory beanFactory, String... basePackage);
}
