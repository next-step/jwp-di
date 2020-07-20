package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created By kjs4395 on 2020-07-20
 */
public class ClasspathBeanScanner {

    private final BeanFactory2 beanFactory2;

    public ClasspathBeanScanner(BeanFactory2 beanFactory) {
        this.beanFactory2 = beanFactory;
    }

    public void doScan(Object...basePackage) {
        Reflections reflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());

        Set<Class<?>> preInstanticateClazz = getTypesAnnotatedWith(reflections, Controller.class, Service.class, Repository.class);

        beanFactory2.register(getBeanInfos(preInstanticateClazz));
    }

    private Set<BeanInfo> getBeanInfos(Set<Class<?>> preInstanticateClazz) {
        return preInstanticateClazz.stream()
                .map(clazz -> {
                    return new BeanInfo(clazz, clazz, BeanInvokeType.CONSTRUCTOR,
                            BeanFactoryUtils.getInjectedConstructor(clazz), null);})
                .collect(Collectors.toSet());
    }

    private Set<Class<?>> getTypesAnnotatedWith(Reflections reflections, Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }
}
