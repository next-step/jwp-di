package core.di.factory;

import core.annotation.*;
import core.annotation.web.Controller;
import core.util.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ClassBeanScanner implements BeanScanner {

    private static final Logger logger = LoggerFactory.getLogger(ClassBeanScanner.class);

    private BeanDefinitionRegistry beanDefinitionRegistry;

    private Class<? extends Annotation>[] annotations = new Class[] { Controller.class, Service.class, Repository.class, Component.class, Configuration.class};

    public ClassBeanScanner(BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    @Override
    public void scan(Object... basePackages) {
        scanPackages(new LinkedHashSet<>(), basePackages);
    }

    public void scanPackages(Set<Class<?>> scannedClasses, Object... basePackages) {

        Reflections reflections = new Reflections(basePackages, new TypeAnnotationsScanner(), new SubTypesScanner(false), new MethodAnnotationsScanner());
        Set<Class<?>> getAnnotatedClasses = ReflectionUtils.getAnnotatedClasses(reflections, annotations);

        for (Class<?> targetClass : getAnnotatedClasses) {
            if(scannedClasses.contains(targetClass)) {
                continue;
            }

            String name = getComponentName(targetClass);
            ClassBeanDefinition beanDefinition = new ClassBeanDefinition(targetClass, name);
            scannedClasses.add(targetClass);
            beanDefinitionRegistry.registerDefinition(beanDefinition);

            if(targetClass.isAnnotationPresent(ComponentScan.class)) {
                String[] scanPackages = getComponentScanPackage(targetClass);
                scanPackages(scannedClasses, scanPackages);
            }
        }
    }

    private String getComponentName(Class<?> targetClass) {
        Component component = AnnotatedElementUtils.findMergedAnnotation(targetClass, Component.class);
        return "".equals(component.value()) ? targetClass.getName() : component.value();
    }

    private String[] getComponentScanPackage(Class<?> targetClass) {
        if(!(targetClass.isAnnotationPresent(Configuration.class) && targetClass.isAnnotationPresent(ComponentScan.class)) ) {
            return new String[]{};
        }

        ComponentScan componentScan = targetClass.getAnnotation(ComponentScan.class);

        return componentScan.value();
    }
}
