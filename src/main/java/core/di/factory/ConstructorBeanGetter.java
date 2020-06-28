package core.di.factory;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;

@Slf4j
public class ConstructorBeanGetter implements BeanGetter {
    private Set<Class<?>> preInstanticateBeans;
    private Map<Class<?>, Object> beans;

    public ConstructorBeanGetter(Set<Class<?>> preInstanticateBeans, Map<Class<?>, Object> beans) {
        this.preInstanticateBeans = preInstanticateBeans;
        this.beans = beans;
    }

    @Override
    public Object getBean(Class<?> beanClass) {
        try {
            Optional<Constructor> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(beanClass);

            if (hasNoArgument(injectedConstructor)) {
                return getNoArgBeanInstance(beanClass, injectedConstructor);
            }

            return getArgBeanInstance(injectedConstructor.get());
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private boolean hasNoArgument(Optional<Constructor> constructor) {
        return !constructor.isPresent() || ArrayUtils.isEmpty(constructor.get().getParameters());
    }

    private Object getNoArgBeanInstance(Class<?> beanClass, Optional<Constructor> constructor) {
        if (beans.containsKey(beanClass)) {
            return beans.get(beanClass);
        }

        return constructor
                .map(c -> BeanUtils.instantiateClass(c))
                .orElseGet(() -> BeanUtils.instantiateClass(beanClass));
    }

    private Object getArgBeanInstance(Constructor constructor) {
        Parameter[] parameters = constructor.getParameters();
        List<Object> arguments = Lists.newArrayList();

        for (Parameter parameter : parameters) {
            Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(parameter.getType(), preInstanticateBeans);

            if (beans.containsKey(concreteClass)) {
                arguments.add(beans.get(concreteClass));
            }
            else {
                Object beanInstance = getBean(concreteClass);
                beans.put(concreteClass, beanInstance);
                arguments.add(beanInstance);
            }
        }

        return BeanUtils.instantiateClass(constructor, arguments.toArray(new Object[0]));
    }
}
