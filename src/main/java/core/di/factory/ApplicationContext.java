package core.di.factory;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import core.annotation.ComponentScan;
import core.annotation.Configuration;

public class ApplicationContext {

    private final BeanFactory beanFactory;
    
    private final ConfigurationBeanDefinitionReader configurationBeanDefinitionReader;
    private final ClassPathBeanDefinitionScanner classPathBeanDefinitionScanner;
    
    private final Set<Class<?>> configurationClasses = Sets.newHashSet();

    public ApplicationContext(Class<?>... classes) {
        this.beanFactory = new BeanFactory();
        this.configurationBeanDefinitionReader = new ConfigurationBeanDefinitionReader(this.beanFactory);
        this.classPathBeanDefinitionScanner = new ClassPathBeanDefinitionScanner(this.beanFactory);
        
        loadConfigurationBeanDefinition(classes);

        beanFactory.initialize();
    }
    
    public <T> T getBean(Class<T> clazz) {
		return beanFactory.getBean(clazz);
	}
    
    public Map<Class<?>, Object> getBeansByAnnotation(Class<? extends Annotation> annotationClass){
    	return beanFactory.getBeansByAnnotation(annotationClass);
    }

    private void loadConfigurationBeanDefinition(Class<?>[] configurationClasses) {
		this.configurationBeanDefinitionReader.loadBeanDefinitions(configurationClasses);
		loadComponentScan(configurationClasses);
	}
    
    private void loadComponentScan(Class<?>[] classes) {
        for(Class<?> clazz : classes) {
            loadComponentScan(clazz);
        }
    }

    private void loadComponentScan(Class<?> clazz) {
    	
    	if(configurationClasses.contains(clazz)) {
    		return;
    	}
    	
    	configurationClasses.add(clazz);
    	
        if(clazz.isAnnotationPresent(Configuration.class) && clazz.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScan = clazz.getAnnotation(ComponentScan.class);
            String[] basePackages = getBasePackage(componentScan);
            if(basePackages.length == 0) {
            	return;
            }
            
            this.classPathBeanDefinitionScanner.loadBeanDefinitions((Object[])basePackages);
            
            Set<Class<?>> configurationClasses = this.classPathBeanDefinitionScanner.getConfigurationClasses(basePackages);
            loadConfigurationBeanDefinition(configurationClasses.toArray(new Class<?>[configurationClasses.size()]));
        }
    }
    
    private String[] getBasePackage(ComponentScan componentScan) {
    	final String[] empty = new String[0];
    	
    	if(componentScan == null) {
    		return empty;
    	}
    	
    	if(componentScan.value().length != 0) {
    		return componentScan.value();
    	}
    	
    	return componentScan.basePackages();
    }
}
