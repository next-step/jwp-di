package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import org.springframework.beans.BeanUtils;
import support.exception.ExceptionWrapper;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

public class BeanFactory {

    private final Set<Class<?>> scannedAnnotatedTypes;
    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(final Set<Class<?>> scannedAnnotatedTypes) {
        this.scannedAnnotatedTypes = scannedAnnotatedTypes;
    }

    public void initialize() {
        scannedAnnotatedTypes.forEach(ExceptionWrapper.consumer(this::putInstance));
    }

    private void putInstance(Class<?> type) {
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

    private Object instanticate(Class<?> type) {
        // Bean 저장소에 clazz에 해당하는 인스턴스가 이미 존재하면 해당 인스턴스 반환
        final Object found = beans.get(type);
        if (found != null) {
            return found;
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
        final Object[] parameters = Arrays.stream(constructor.getParameterTypes())
                .map(parameterType -> BeanFactoryUtils.findConcreteClass(parameterType, scannedAnnotatedTypes))
                .map(ExceptionWrapper.function(this::instanticate))
                .toArray();

        return BeanUtils.instantiateClass(constructor, parameters);
    }
}
