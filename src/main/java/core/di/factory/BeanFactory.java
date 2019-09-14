package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Bean;
import core.annotation.web.Controller;
import core.mvc.tobe.BeanScanner;
import core.mvc.tobe.ConfigurationScanner;
import org.springframework.beans.BeanUtils;
import support.exception.ExceptionWrapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BeanFactory {

    private final Map<Class<?>, Object> methodTypes = Maps.newHashMap();
    private final Set<Class<?>> scannedAnnotatedTypes;
    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(final BeanScanner... beanScanners) {
        final Set<BeanScanner> scanners = Arrays.stream(beanScanners)
                .collect(Collectors.toSet());
        final ConfigurationScanner configurationScanner = findConfigurationScanner(scanners);

        this.methodTypes.putAll(getMethodTypes(configurationScanner));
        this.scannedAnnotatedTypes = scanners.stream()
                .flatMap(beanScanner -> beanScanner.getTypes().stream())
                .collect(Collectors.toSet());
    }

    @SafeVarargs
    public BeanFactory(final Set<Class<?>>... scannedAnnotatedTypes) {
        final ConfigurationScanner configurationScanner = new ConfigurationScanner();

        this.methodTypes.putAll(getMethodTypes(configurationScanner));
        this.scannedAnnotatedTypes = Stream.of(scannedAnnotatedTypes)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private ConfigurationScanner findConfigurationScanner(final Set<BeanScanner> scanners) {
        return (ConfigurationScanner) scanners.stream()
                .filter(scanner -> scanner instanceof ConfigurationScanner)
                .findFirst()
                .orElse(null);
    }

    private Map<Class<?>, Object> getMethodTypes(final ConfigurationScanner configurationScanner) {
        if (configurationScanner == null) {
            return Collections.emptyMap();
        }

        return configurationScanner.getTypes()
                .stream()
                .flatMap(type -> Arrays.stream(type.getDeclaredMethods()))
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .collect(Collectors.toMap(
                        ExceptionWrapper.function(this::getReturnType),
                        ExceptionWrapper.function(method -> instanticate(method.getDeclaringClass()))));
    }

    private Class<?> getReturnType(Method method) throws IllegalAccessException, InvocationTargetException {
        final Class<?> returnType = method.getReturnType();
        if (returnType.isInterface()) {
            final Object configuration = instanticate(method.getDeclaringClass());

            return method.invoke(configuration).getClass();
        }

        return returnType;
    }

    public void initialize() {
        Stream.concat(scannedAnnotatedTypes.stream(), methodTypes.keySet().stream())
                .forEach(ExceptionWrapper.consumer(this::putInstance));
    }

    private void putInstance(Class<?> type) throws IllegalAccessException, InvocationTargetException {
        beans.put(type, instanticate(type));
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public Set<Class<?>> getControllers() {
        return scannedAnnotatedTypes.stream()
                .filter(type -> type.isAnnotationPresent(Controller.class))
                .collect(Collectors.toSet());
    }

    private Object instanticate(Class<?> type) throws IllegalAccessException, InvocationTargetException {
        // Bean 저장소에 clazz에 해당하는 인스턴스가 이미 존재하면 해당 인스턴스 반환
        final Object found = beans.get(type);
        if (found != null) {
            return found;
        }

        final Object configuration = methodTypes.get(type);
        if (configuration != null) {
            final Method beanMethod = Arrays.stream(configuration.getClass().getDeclaredMethods())
                    .filter(method -> method.getReturnType().isAssignableFrom(type))
                    .findFirst()
                    .orElseThrow(IllegalStateException::new);

            final Object[] parameters = Arrays.stream(beanMethod.getParameterTypes())
                .map(parameterType -> BeanFactoryUtils.findConcreteClass(parameterType, methodTypes.keySet()))
                .map(ExceptionWrapper.function(this::instanticate))
                .toArray();

            return beanMethod.invoke(configuration, parameters);
        }

        // clazz에 @Inject가 설정되어 있는 생성자를 찾는다. BeanFactoryUtils 활용
        final Constructor<?> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(type);

        // @Inject로 설정한 생성자가 없으면 Default 생성자로 인스턴스 생성 후 Bean 저장소에 추가 후 반환
        if (injectedConstructor == null) {
            return instantiateClass(type);
        }

        // @Inject로 설정한 생성자가 있으면 찾은 생성자를 활용해 인스턴스 생성 후 Bean 저장소에 추가 후 반환
        return instantiateConstructor(injectedConstructor);
    }

    private Object instantiateClass(Class<?> type) {
        return BeanUtils.instantiateClass(type);
    }

    private Object instantiateConstructor(Constructor<?> constructor) {
        final Set<Class<?>> types = Stream.concat(scannedAnnotatedTypes.stream(), methodTypes.keySet().stream())
                .collect(Collectors.toSet());

        final Object[] parameters = Arrays.stream(constructor.getParameterTypes())
                .map(parameterType -> BeanFactoryUtils.findConcreteClass(parameterType, types))
                .map(ExceptionWrapper.function(this::instanticate))
                .toArray();

        return BeanUtils.instantiateClass(constructor, parameters);
    }
}
