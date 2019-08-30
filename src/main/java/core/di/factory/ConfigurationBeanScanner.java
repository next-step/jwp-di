package core.di.factory;

import core.annotation.ComponentScan;

public class ConfigurationBeanScanner {

    private BeanFactory beanFactory;
    private String[] basePath = null;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Class<?> configuratorClass){
        beanFactory.initInstanticateBeans(configuratorClass);
        basePath = configuratorClass.getAnnotation(ComponentScan.class).value();
    }

    public String[] getBasePath(){
        return this.basePath;
    }
}
