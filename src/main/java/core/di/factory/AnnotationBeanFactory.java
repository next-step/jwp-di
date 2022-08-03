package core.di.factory;

import core.annotation.Inject;
import core.annotation.web.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnotationBeanFactory extends AbstractBeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationBeanFactory.class);

    public AnnotationBeanFactory() {
    }

    @Override
    public void register(Set<Class<?>> preInstanticateBeans) {
        preInstanticateBeans.forEach(bean -> initIndependentBean(bean));
        preInstanticateBeans.forEach(bean -> initDependentBean(bean));
    }

    private void initIndependentBean(Class<?> bean) {
        Arrays.stream(bean.getDeclaredConstructors())
              .filter(constructor -> !constructor.isAnnotationPresent(Inject.class))
              .forEach(__ -> {
                  Object obj = BeanUtils.instantiateClass(bean);
                  Arrays.stream(bean.getInterfaces()).forEach(aClass -> beans.put(aClass, obj));
                  beans.put(bean, obj);
              });
    }

    private void initDependentBean(Class<?> bean) {
        Arrays.stream(bean.getDeclaredConstructors())
              .filter(constructor -> constructor.isAnnotationPresent(Inject.class))
              .forEach(constructor -> createDependentBean(bean, constructor));
    }

    private void createDependentBean(Class<?> bean, Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Arrays.stream(parameterTypes)
              .filter(parameterType -> !beans.containsKey(parameterType))
              .forEach(parameterType -> initDependentBean(parameterType));

        List<Object> parameters = Arrays.stream(parameterTypes)
                                     .map(parameterType -> beans.get(parameterType))
                                     .collect(Collectors.toList());
        Object obj = BeanUtils.instantiateClass(constructor, parameters.toArray());
        Arrays.stream(bean.getInterfaces()).forEach(aClass -> beans.put(aClass, obj));
        beans.put(bean, obj);
    }

    public Set<Object> getControllerBeans() {
        return beans.keySet()
                     .stream()
                     .filter(beanClass -> beanClass.isAnnotationPresent(Controller.class))
                     .map(controllerBean -> beans.get(controllerBean))
                     .collect(Collectors.toSet());
    }
}
