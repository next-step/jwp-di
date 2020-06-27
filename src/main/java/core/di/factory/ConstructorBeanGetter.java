package core.di.factory;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
        Constructor constructor = BeanFactoryUtils.getInjectedConstructors(beanClass);

        try {
            if (hasNoArgument(constructor)) {
                return getNoArgBeanInstance(beanClass, constructor);
            }

            return getArgBeanInstance(constructor);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private boolean hasNoArgument(Constructor constructor) {
        return Objects.isNull(constructor) || ArrayUtils.isEmpty(constructor.getParameters());
    }

    private Object getNoArgBeanInstance(Class<?> beanClass, Constructor constructor) {
        if (beans.containsKey(beanClass)) {
            return beans.get(beanClass);
        }

        if (Objects.isNull(constructor)) {
            return BeanUtils.instantiateClass(beanClass);
        }

        return BeanUtils.instantiateClass(constructor);
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
