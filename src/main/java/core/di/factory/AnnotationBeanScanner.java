package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.Bean;
import org.reflections.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Set;

public class AnnotationBeanScanner {
    private Class<?> clazz;

    public AnnotationBeanScanner(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Configuration is empty");
        }

        this.clazz = clazz;
    }

    public void scan(BeanFactory beanFactory) {
        Set<BeanDefinition> beanDefs = Sets.newHashSet();
        for (Method method : getMethods()) {
            beanDefs.add(new AnnotationBeanDefinition(method));
        }
        beanFactory.addBeanDefs(beanDefs);
    }

    private Set<Method> getMethods() {
        return ReflectionUtils.getMethods(this.clazz, ReflectionUtils.withAnnotation(Bean.class));
    }
}
