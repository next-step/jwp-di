package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Bean;
import core.annotation.Configuration;
import core.annotation.web.Controller;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstantiateBeans = new HashSet<>();

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory() {
    }

    public BeanFactory(Set<Class<?>> preInstantiateBeans) {
        this.preInstantiateBeans = preInstantiateBeans;
    }

    public void register(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Configuration.class)) {
            return;
        }

        List<Method> beanMethods = Arrays.stream(clazz.getDeclaredMethods())
            .filter(cls -> cls.isAnnotationPresent(Bean.class))
            .collect(Collectors.toList());

        for (Method beanMethod : beanMethods) {
            Class<?> beanClazz = beanMethod.getReturnType();
            Object[] arguments = getArguments(beanMethod);

            try {
                beans.putIfAbsent(beanClazz, beanMethod.invoke(BeanUtils.instantiateClass(clazz), arguments));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        try {
            createDependencies();
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void createDependencies() {
        if (Objects.isNull(preInstantiateBeans) || preInstantiateBeans.isEmpty()) {
            return;
        }

        for (Class<?> clazz : preInstantiateBeans) {
            beans.putIfAbsent(clazz, recursive(clazz));
        }
    }

    private Object recursive(Class<?> clazz) {
        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(clazz);

        if (injectedConstructor == null) {
            return BeanUtils.instantiateClass(clazz);
        }

        Class<?>[] parameterTypes = injectedConstructor.getParameterTypes();

        Object[] arguments = getArguments(parameterTypes);

        try {
            return injectedConstructor.newInstance(arguments);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    private Object[] getArguments(Class<?>[] parameterTypes) {
        Object[] params = new Object[parameterTypes.length];
        for (int i = 0; i < params.length; ++i) {
            if (beans.containsKey(parameterTypes[i])) {
                params[i] = beans.get(parameterTypes[i]);
                continue;
            }
            Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(parameterTypes[i], this.preInstantiateBeans);
            params[i] = recursive(concreteClass);
        }
        return params;
    }

    private Object[] getArguments(Method method) {
        List<Object> arguments = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            Object autowireBean = this.getBean(parameter.getType());
            if (autowireBean == null) {
                throw new RuntimeException("의존 관계를 주입할 Bean 이 존재하지 않습니다.");
            }
            arguments.add(autowireBean);
        }
        return arguments.toArray();
    }

    public List<Object> getControllers() {
        return this.beans.entrySet()
            .stream()
            .filter(classObjectEntry -> classObjectEntry.getKey()
                .isAnnotationPresent(Controller.class))
            .map(Entry::getValue)
            .collect(Collectors.toList());
    }

    public void addPreInstantiateBeans(Set<Class<?>> preInstantiateBeans) {
        this.preInstantiateBeans.addAll(preInstantiateBeans);
    }

}
