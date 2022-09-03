package core.di.factory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import core.annotation.web.Controller;
import core.exception.NoSuchBeanConstructorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BeanFactory implements Subject<BeanRegister> {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private final Set<BeanRegister> beanRegisters = Sets.newHashSet();

    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory() {
    }

    public BeanFactory(Set<Class<?>> preInstanticateClazz) {
        for (Class<?> clazz : preInstanticateClazz) {
            ClassBeanRegister beanRegister = new ClassBeanRegister(clazz);
            register(beanRegister);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        publishing(br -> {
            Object instance = createInstance(br);
            addBean(br, instance);
        });
    }

    private void addBean(BeanRegister register, Object instance) {
        for (Class<?> type : register.interfaces()) {
            beans.put(type, instance);
        }
    }

    public void register(BeanRegister... beanRegisters) {
        for (BeanRegister beanRegister : beanRegisters) {
            this.beanRegisters.add(beanRegister);
            beanRegister.subscribe(this);
            beanRegister.initialize();
        }
    }

    private Object createInstance(BeanRegister beanRegister) {
        List<Object> parameters = new ArrayList<>();

        for (Class<?> typeClass : beanRegister.getParameterTypes()) {
            parameters.add(getParameterByClass(typeClass));
        }

        try {
            return beanRegister.newInstance(parameters.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error("Bean 생성에 실패했습니다. [target:{}, cause:{}]", beanRegister.type().getName(), e.getCause());
            throw new RuntimeException(e);
        }
    }

    private Object getParameterByClass(Class<?> typeClass) {
        Object bean = getBean(typeClass);

        if (Objects.nonNull(bean)) {
            return bean;
        }

        BeanRegister beanRegister = beanRegisters.stream()
                .filter(br -> br.type().equals(typeClass))
                .findFirst()
                .orElseThrow(() -> new NoSuchBeanConstructorException(typeClass));

        return createInstance(beanRegister);
    }

    public Set<Class<?>> getControllers() {
        return beans.keySet().stream()
                .filter(key -> key.isAnnotationPresent(Controller.class))
                .collect(Collectors.toSet());
    }

    public Set<Class<?>> getInstanticateBeans() {
        return beanRegisters.stream()
                .map(BeanRegister::type)
                .collect(Collectors.toSet());
    }

    @Override
    public void publishing(Consumer<BeanRegister> action) {
        beanRegisters.stream()
                .sorted(Comparator.comparing(BeanRegister::priority).thenComparing(BeanRegister::getParameterCount))
                .forEach(action);
    }
}
