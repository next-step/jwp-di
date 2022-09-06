package core.di;

import com.google.common.collect.Lists;
import core.annotation.Bean;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.NoSuchObjectException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigurationBeanScanner {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private final BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Class<?> configuration) throws InvocationTargetException, IllegalAccessException {

        if (!configuration.isAnnotationPresent(Configuration.class)) {
            return ;
        }

        List<Method> declaredMethods = Arrays.stream(ReflectionUtils.getDeclaredMethods(configuration))
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .collect(Collectors.toList());

        Object configBean = BeanUtils.instantiateClass(configuration);

        for (Method declaredMethod : declaredMethods) {
            Class<?>[] parameterTypes = declaredMethod.getParameterTypes();

            Object[] parameters = getParametersOfMethod(parameterTypes);

            Object bean = declaredMethod.invoke(configBean, parameters);
            beanFactory.register(declaredMethod.getReturnType(), bean);
        }
    }

    private Object[] getParametersOfMethod(Class<?>[] parameterTypes) {
        List<Object> objects = Lists.newArrayList();
        for (Class<?> parameterType : parameterTypes) {
            Object injectedBean = beanFactory.getBean(parameterType);
            if (injectedBean == null) {
                throw new RuntimeException("빈이 존재하지 않습니다.");
            }
            objects.add(injectedBean);
        }
        return objects.toArray();
    }
}
