package core.di.factory;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class MethodBeanDefinitionResolver extends AbstractBeanDefinitionResolver<Method> {
    private final Method method;
    private final Object parent;

    public MethodBeanDefinitionResolver(
        Set<Class<?>> rootTypes,
        Class<?> type,
        Map<Class<?>, BeanDefinition> beanDefinitions,
        Map<Class<?>, BeanDefinitionResolver> resolvers,
        Method method,
        Object parent
    ) {
        super(rootTypes, type, beanDefinitions, resolvers);
        this.parent = parent;
        this.method = method;
    }

    @Override
    public BeanDefinition resolve() {
        try {
            if (ArrayUtils.isEmpty(method.getParameters())) {
                return buildBeanDefinition(null);
            }

            return getParameterizedBeanDefinition(method.getParameters());
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public Constructor getConstructor() {
        return null;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object getParent() {
        return parent;
    }

    @Override
    public Class<?> getType() {
        return type;
    }
}
