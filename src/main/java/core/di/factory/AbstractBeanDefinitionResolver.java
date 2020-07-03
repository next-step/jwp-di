/*
package core.di.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import core.di.beans.definition.BeanDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static core.di.factory.BeanFactory.TARGET_ANNOTATION_TYPES;

@Slf4j
public abstract class AbstractBeanDefinitionResolver<T> implements BeanDefinitionResolver<T> {
    protected final Map<Class<?>, BeanDefinitionResolver> resolvers;
    protected final Set<Class<?>> rootTypes;
    protected final Class<?> type;

    protected final Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();

    public AbstractBeanDefinitionResolver(
        Set<Class<?>> rootTypes,
        Class<?> type,
        Map<Class<?>, BeanDefinition> beanDefinitions,
        Map<Class<?>, BeanDefinitionResolver> resolvers) {
        this.rootTypes = rootTypes;
        this.type = type;
        this.beanDefinitions = beanDefinitions;
        this.resolvers = resolvers;
    }

    protected BeanDefinition getParameterizedBeanDefinition(Parameter[] parameters) {
        List<BeanDefinition> arguments = Lists.newArrayList();

        for (Parameter parameter : parameters) {
            Class<?> parameterType = getParameterType(parameter);

            if (beanDefinitions.containsKey(parameterType)) {
                arguments.add(beanDefinitions.get(parameterType));
            }
            else {
                BeanDefinition beanDefinition = getResolver(parameterType).resolve();
                beanDefinitions.put(parameterType, beanDefinition);
                arguments.add(beanDefinition);
            }
        }

        return buildBeanDefinition(arguments);
    }

    private Class<?> getParameterType(Parameter parameter) {
        Class<?> type = BeanFactoryUtils.findConcreteClass(parameter.getType(), rootTypes);

        if (Objects.nonNull(type)) {
            return type;
        }

        return parameter.getType();
    }

    private BeanDefinitionResolver getResolver(Class<?> type) {
        if (resolvers.containsKey(type)) {
            return resolvers.get(type);
        }

        return getNewResolver(type);
    }

    private BeanDefinitionResolver getNewResolver(Class<?> type) {
        BeanDefinitionResolver resolver = null;

        if (TARGET_ANNOTATION_TYPES.contains(type)) {
            resolver = new ConstructorBeanDefinitionResolver(rootTypes, type, beanDefinitions, resolvers);
            resolvers.put(type, resolver);
        }
        else {
            Set<Method> methods = BeanFactoryUtils.getAnnotatedBeanMethods(type);
            Object parent = BeanUtils.instantiateClass(type);

            for (Method method : methods) {
                resolver = new MethodBeanDefinitionResolver(rootTypes, type, beanDefinitions, resolvers, method, parent);
                resolvers.put(type, resolver);
            }
        }

        return resolver;
    }

    protected BeanDefinition buildBeanDefinition(
        List<BeanDefinition> arguments
    ) {
        log.debug("type: {}, parent: {}, constructor: {}, method: {}", getType(), getParent(), getConstructor(), getMethod());
        return BeanDefinition.builder()
            .type(getType())
            .parent(getParent())
            .constructor(getConstructor())
            .method(getMethod())
            .children(arguments)
            .build();
    }

    abstract Constructor getConstructor();
    abstract Method getMethod();
    abstract Object getParent();
}
*/
