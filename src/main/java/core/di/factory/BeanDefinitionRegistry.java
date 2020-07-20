package core.di.factory;

import core.di.BeanDefinitions;

/**
 * Created by iltaek on 2020/07/19 Blog : http://blog.iltaek.me Github : http://github.com/iltaek
 */
public interface BeanDefinitionRegistry {

    void registerBeanDefinitions(BeanDefinitions beanDefinitions);
}
