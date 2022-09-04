package next.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import com.google.common.collect.Lists;

import core.annotation.Bean;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;

public class ConfigurationBeanScanner {

    private final BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(Configuration.class)) {
            return;
        }

        List<Method> beanMethods = Arrays.stream(configClass.getDeclaredMethods())
            .filter(method -> method.isAnnotationPresent(Bean.class))
            .collect(Collectors.toList());

        Object configBean = BeanUtils.instantiateClass(configClass);

        for (Method beanMethod : beanMethods) {
            Object[] arguments = getMethodArguments(beanMethod);
            try {
                Object bean = beanMethod.invoke(configBean, arguments);
                beanFactory.addBean(beanMethod.getReturnType(), bean);

            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Object[] getMethodArguments(Method beanMethod) {
        List<Object> arguments = Lists.newArrayList();
        Parameter[] parameters = beanMethod.getParameters();
        for (Parameter parameter : parameters) {
            Object autowireBean = beanFactory.getBean(parameter.getType());
            if (autowireBean == null) {
                throw new RuntimeException("의존 관계를 주입할 Bean이 존재하지 않습니다.");
            }
            arguments.add(autowireBean);
        }
        return arguments.toArray();
    }
}
