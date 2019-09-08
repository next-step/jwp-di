package core.mvc.tobe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.exception.CreateInstanceFailException;

import java.lang.reflect.Method;
import java.util.function.Function;

public class ConfigurationBeanDefinition implements BeanDefinition{
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationBeanDefinition.class);

    private Object configurationObject;
    private Method beanCreateMethod;

    public ConfigurationBeanDefinition(Object configurationObject, Method beanCreateMethod) {
        this.configurationObject = configurationObject;
        this.beanCreateMethod = beanCreateMethod;
    }

    @Override
    public Class<?> getBeanClass() {
        return this.beanCreateMethod.getReturnType();
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return this.beanCreateMethod.getParameterTypes();
    }

    @Override
    public Function<Object[], Object> getInstantiateFunction() {
        return (parameters) -> {
            try {
                return beanCreateMethod.invoke(this.configurationObject, parameters);
            } catch (ReflectiveOperationException e) {
                logger.error(e.getMessage());
                throw new CreateInstanceFailException();
            }
        };
    }
}
