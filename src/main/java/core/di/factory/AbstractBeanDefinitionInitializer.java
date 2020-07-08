package core.di.factory;

/**
 * @author KingCjy
 */
public abstract class AbstractBeanDefinitionInitializer implements BeanInitializer {

    @Override
    public Object instantiate(BeanDefinition beanDefinition, BeanFactory beanFactory) {
        return support(beanDefinition) ? instantiateBean(beanDefinition, beanFactory) : null;
    }

    abstract public Object instantiateBean(BeanDefinition beanDefinition, BeanFactory beanFactory);
}
