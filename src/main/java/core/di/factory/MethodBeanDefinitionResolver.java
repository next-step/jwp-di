package core.di.factory;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@Slf4j
public class MethodBeanDefinitionResolver implements BeanDefinitionResolver {
    private Set<Class<?>> preInstanticateBeans;
    private Map<Class<?>, BeanDefinition> beanDefinitions;

    public MethodBeanDefinitionResolver(Set<Class<?>> preInstanticateBeans, Map<Class<?>, BeanDefinition> beanDefinitions) {
        this.preInstanticateBeans = preInstanticateBeans;
        this.beanDefinitions = beanDefinitions;
    }

    @Override
    public BeanDefinition resolve(Class<?> beanClass) {
        try {
            Set<Method> methods = BeanFactoryUtils.getAnnotatedBeanMethod(beanClass);

            if (CollectionUtils.isEmpty(methods)) {
                return null;
            }

            for (Method method : methods) {
                Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(method.getReturnType(), preInstanticateBeans);

                if (!beanDefinitions.containsKey(concreteClass)) {
                    beanDefinitions.put(concreteClass, resolve(concreteClass, method));
                }
            }
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private BeanDefinition resolve(Class<?> type, Method method) {
        if (ArrayUtils.isEmpty(method.getParameters())) {
            return buildBeanDefinition(type, null, method, null);
        }

        return getParameterizedBeanDefinition(type, method);
    }

    private BeanDefinition getParameterizedBeanDefinition(Class<?> type, Method method) {
        Parameter[] parameters = method.getParameters();
        List<BeanDefinition> arguments = Lists.newArrayList();

        for (Parameter parameter : parameters) {
            Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(parameter.getType(), preInstanticateBeans);

            if (beanDefinitions.containsKey(concreteClass)) {
                arguments.add(beanDefinitions.get(concreteClass));
            }
            else {
                BeanDefinition beanDefinition = resolve(concreteClass);
                beanDefinitions.put(concreteClass, beanDefinition);
                arguments.add(beanDefinition);
            }
        }

        return buildBeanDefinition(type, constructor, arguments);
    }

    private BeanDefinition buildBeanDefinition(
        Class<?> type,
        Constructor constructor,
        Method method,
        List<BeanDefinition> arguments
    ) {
        return BeanDefinition.builder()
            .type(type)
            .annotations(Arrays.asList(type.getDeclaredAnnotations()))
            .constructor(constructor)
            .method(method)
            .children(arguments)
            .build();
    }
}
