/*
package core.di.factory;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
public class ConstructorBeanDefinitionResolver extends AbstractBeanDefinitionResolver<Constructor> {
    private final Constructor constructor;

    public ConstructorBeanDefinitionResolver(
        Set<Class<?>> rootTypes,
        Class<?> type,
        Map<Class<?>, BeanDefinition> beanDefinitions,
        Map<Class<?>, BeanDefinitionResolver> resolvers) {
        super(rootTypes, type, beanDefinitions, resolvers);
        this.constructor = BeanFactoryUtils.getInjectedConstructor(type);
    }

    @Override
    public BeanDefinition resolve() {
        try {
            if (hasNoArgument(constructor)) {
                return buildBeanDefinition(null);
            }

            return getParameterizedBeanDefinition(constructor.getParameters());
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    private boolean hasNoArgument(Constructor constructor) {
        return Objects.isNull(constructor) || ArrayUtils.isEmpty(constructor.getParameters());
    }

    @Override
    public Constructor getConstructor() {
        return constructor;
    }

    @Override
    public Method getMethod() {
        return null;
    }

    @Override
    public Object getParent() {
        return null;
    }

    @Override
    public Class<?> getType() {
        return type;
    }
}
*/
