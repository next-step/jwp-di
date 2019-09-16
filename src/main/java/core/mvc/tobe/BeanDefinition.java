package core.mvc.tobe;

import java.util.function.Function;

public interface BeanDefinition {
    Class<?> getBeanClass();

    Class<?>[] getParameterTypes();

    Function<Object[], Object> getInstantiateFunction();
}
