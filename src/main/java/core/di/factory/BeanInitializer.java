package core.di.factory;

import jdk.internal.jline.internal.Nullable;

/**
 * @author KingCjy
 */
public interface BeanInitializer {
    boolean support(BeanDefinition beanDefinition);

    @Nullable
    Object instantiate(BeanDefinition beanDefinition, BeanFactory beanFactory);
}
