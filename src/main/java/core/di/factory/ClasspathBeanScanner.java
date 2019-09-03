package core.di.factory;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ClasspathBeanScanner {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationBeanScanner.class);

    private BeanFactory beanFactory;
    private Reflections reflections;


    public ClasspathBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void doScan(String... path){
        reflections = new Reflections(path);

        try {
            Set<Class<?>> preInstanticateClazz = BeanFactoryUtils.getTypesAnnotatedWith(reflections,
                    Controller.class, Service.class, Repository.class);
            beanFactory.initialize(preInstanticateClazz);
        }catch (Exception e){
            log.error("Configurator Bean Create Error {}", e.getMessage());
        }
    }
}
