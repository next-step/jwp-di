package core.di.factory;

/**
 * @author KingCjy
 */
public interface BeanInitializer {
    boolean support(BeanDefinition beanDefinition);
    Object instantiate(BeanDefinition beanDefinition, BeanFactory beanFactory);
}
