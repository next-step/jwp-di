package core.di.tobe.scanner;

import core.di.tobe.bean.BeanDefinition;

import java.util.Set;

public interface BeanScanner {

    Set<BeanDefinition> enroll();
}
