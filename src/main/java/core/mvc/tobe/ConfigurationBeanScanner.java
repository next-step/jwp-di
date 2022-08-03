package core.mvc.tobe;

import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import core.util.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

public class ConfigurationBeanScanner implements BeanScanner {

    private BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void scan(Object... basePackage) {
        Reflections reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());
        this.beanFactory.register(ReflectionUtils.getTypesAnnotatedWith(reflections, Configuration.class));
    }
}
