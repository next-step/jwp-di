package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.Configuration;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;
import org.reflections.scanners.MemberUsageScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Set;

public class BeanScanner {

    private static final Logger logger = LoggerFactory.getLogger(BeanScanner.class);

    public Set<Class<?>> scan(Object... basePackage) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> preInstantiateClazz = getTypesAnnotatedWith(reflections, Controller.class, Service.class, Repository.class);

        return preInstantiateClazz;
    }

    public Set<Class<?>> scanConfiguration() {
        Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(""))
                .addScanners(new MemberUsageScanner(), new MethodAnnotationsScanner(), new MethodParameterScanner()));
        Set<Class<?>> configurationClasses = reflections.getTypesAnnotatedWith(Configuration.class);

        return configurationClasses;
    }

    private Set<Class<?>> getTypesAnnotatedWith(Reflections reflections, Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }
}
