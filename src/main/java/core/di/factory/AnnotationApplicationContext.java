package core.di.factory;

import core.annotation.ComponentScan;
import core.annotation.Configuration;

public class AnnotationApplicationContext extends ApplicationContext {

    public AnnotationApplicationContext(Class<?>... classes) {
        this.beanFactory = new SimpleBeanFactory();
        loadBeanDefinitions(classes);
        this.beanFactory.initialize();
    }

    private void loadBeanDefinitions(Class<?>... classes) {
        final ClasspathBeanScanner classpathBeanScanner = new ClasspathBeanScanner(beanFactory);
        final ConfigurationBeanScanner configurationBeanScanner = new ConfigurationBeanScanner(beanFactory);

        for (Class<?> clazz : classes) {
            if (!clazz.isAnnotationPresent(Configuration.class)) {
                continue;
            }

            if (clazz.isAnnotationPresent(ComponentScan.class)) {
                final ComponentScan componentScan = clazz.getAnnotation(ComponentScan.class);
                classpathBeanScanner.scan(componentScan.basePackages());
            }

            configurationBeanScanner.scan(clazz);
        }
    }


}
