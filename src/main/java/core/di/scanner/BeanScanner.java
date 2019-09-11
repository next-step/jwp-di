package core.di.scanner;

import core.di.bean.BeanDefinition;

import java.util.Set;

public interface BeanScanner {

    Set<BeanDefinition> scan();
}
