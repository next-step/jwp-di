package core.di.factory;


import javax.annotation.Nullable;

/**
 * @author KingCjy
 */
public interface BeanInitializer {
    boolean support(BeanDefinition beanDefinition);

    @Nullable
    Object instantiate(BeanDefinition beanDefinition, BeanFactory beanFactory);
}
