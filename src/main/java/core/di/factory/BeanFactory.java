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
        scannedAnnotatedTypes.stream()
                .sorted(compareInterfacesLength()
                        .reversed())
                .forEach(ExceptionWrapper.consumer(this::putInstance));
    }

    // interface를 구현한 클래스 먼저 객체를 만든다.
    // repository
    private Comparator<Class<?>> compareInterfacesLength() {
        return Comparator.comparingInt(type -> type.getInterfaces().length);
    }

    private void putInstance(Class<?> type) {
        final Object bean = instanticate(type);

        final Optional<Class<?>> firstInterface = Arrays.stream(type.getInterfaces()).findFirst();
        if (firstInterface.isPresent()) {
            beans.put(firstInterface.get(), bean);
            return;
        }

        beans.put(type, bean);
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
                .map(ExceptionWrapper.function(
                        parameterType -> beans.getOrDefault(parameterType, instanticate(parameterType))))
                .toArray();

        return BeanUtils.instantiateClass(constructor, parameters);
    }
}
