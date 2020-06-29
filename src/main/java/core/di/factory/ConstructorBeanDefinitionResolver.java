package core.di.factory;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;

@Slf4j
public class ConstructorBeanDefinitionResolver implements BeanDefinitionResolver {
    private Set<Class<?>> preInstanticateBeans;
    private Map<Class<?>, BeanDefinition> beanDefinitions;

    public ConstructorBeanDefinitionResolver(Set<Class<?>> preInstanticateBeans, Map<Class<?>, BeanDefinition> beanDefinitions) {
        this.preInstanticateBeans = preInstanticateBeans;
        this.beanDefinitions = beanDefinitions;
    }

    @Override
    public BeanDefinition resolve(Class<?> beanClass) {
        try {
            Optional<Constructor> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(beanClass);

            if (hasNoArgument(injectedConstructor)) {
                return getNoArgBeanInstance(beanClass, injectedConstructor);
            }

            return getArgumentedBeanDefinitions(beanClass, injectedConstructor.get());
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private boolean hasNoArgument(Optional<Constructor> constructor) {
        return !constructor.isPresent() || ArrayUtils.isEmpty(constructor.get().getParameters());
    }

    private BeanDefinition getNoArgBeanInstance(Class<?> beanClass, Optional<Constructor> constructor) {
        if (beanDefinitions.containsKey(beanClass)) {
            return beanDefinitions.get(beanClass);
        }

        return constructor
                .map(c -> buildBeanDefinition(beanClass, c,null))
                .orElseGet(() -> buildBeanDefinition(beanClass, null, null));
    }

    private BeanDefinition getArgumentedBeanDefinitions(Class<?> type, Constructor constructor) {
        Parameter[] parameters = constructor.getParameters();
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
        List<BeanDefinition> arguments
    ) {
        return BeanDefinition.builder()
            .type(type)
            .annotations(Arrays.asList(type.getDeclaredAnnotations()))
            .constructor(constructor)
            .children(arguments)
            .build();
    }
}
