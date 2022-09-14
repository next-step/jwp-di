package core.di.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import core.annotation.Bean;
import core.annotation.Configuration;
import core.annotation.web.Controller;
import core.di.factory.exception.NoSuchDefaultConstructorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstanticateBeans = Sets.newHashSet();
    private Set<Class<?>> configurations = Sets.newHashSet();

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory() {
    }

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    /**
     * JdbcQuestionRepository
     * JdbcUserRepository
     * MyQnaService
     * QnaController
     */
    public void initialize() {
        // @Inject가 설정된 클래스를 클래스타입으로 넣으면 해당 클래스 리턴
        logger.info("bean initialize");
        for (Class<?> clazz : preInstanticateBeans) {
            addBeans(clazz);
        }

        for (Class<?> configuration : configurations) {
            addConfiguration(configuration);
        }
    }

    private void addConfiguration(Class<?> configuration) {
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

            Object bean = null;
            try {
                bean = declaredMethod.invoke(configBean, parameters);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            register(declaredMethod.getReturnType(), bean);
        }
    }

    private Object[] getParametersOfMethod(Class<?>[] parameterTypes) {
        List<Object> objects = Lists.newArrayList();
        for (Class<?> parameterType : parameterTypes) {
            Object injectedBean = getBean(parameterType);
            if (injectedBean == null) {
                throw new RuntimeException("빈이 존재하지 않습니다.");
            }
            objects.add(injectedBean);
        }
        return objects.toArray();
    }

    private void addBeans(Class<?> bean) {

        if (beans.containsKey(bean)) {
            return ;
        }

        Constructor<?> constructor = getConstructor(bean);
        List<Class<?>> parameterTypes = getParameterTypes(constructor);

        for (Class<?> parameterType : parameterTypes) {
            addBeans(parameterType);
        }

        Object instance = newInstance(constructor, parameterTypes);
        beans.put(bean, instance);
    }

    private Object newInstance(Constructor<?> constructor, List<Class<?>> parameterTypes) {
        List<Object> objects = Lists.newArrayList();
        for (Class<?> parameterType : parameterTypes) {
            objects.add(beans.get(parameterType));
        }

        return BeanUtils.instantiateClass(constructor, objects.toArray());
    }

    private List<Class<?>> getParameterTypes(Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        List<Class<?>> args = Lists.newArrayList();
        for (Class<?> clazz : parameterTypes) {
            args.add(BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans));
        }
        return args;
    }

    private Constructor<?> getConstructor(Class<?> clazz) {
        Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(clazz);

        if (Objects.nonNull(injectedConstructor)) {
            return injectedConstructor;
        }

        return getDefaultConstructor(clazz);
    }

    private Constructor<?> getDefaultConstructor(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor();
        } catch (Exception e) {
            throw new NoSuchDefaultConstructorException(e);
        }
    }

    public void addAllPreInstantiateBeans(Set<Class<?>> preInstantiateBeans) {
        this.preInstanticateBeans.addAll(preInstantiateBeans);
    }

    public void register(Class<?> configuration, Object bean) {
        beans.put(configuration, bean);
    }

    public void addConfigurations(List<Class<?>> configurations) {
        this.configurations.addAll(configurations);
    }

    public Map<Class<?>, Object> getControllers() {
        return beans.entrySet()
                .stream()
                .filter(entry -> entry.getKey().isAnnotationPresent(Controller.class))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
