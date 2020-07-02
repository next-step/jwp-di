package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private final Set<Class<?>> preInstanticateBeans;
    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        for (Class<?> preInstanticateBean : preInstanticateBeans) {
            process(preInstanticateBean);
        }
    }

    private void process(Class<?> preInstanticateBean) {
        if (beans.containsKey(preInstanticateBean)) {
            return;
        }

        final Object instance = makeInstance(preInstanticateBean);

        beans.put(preInstanticateBean, instance);
    }

    private <T> T makeInstance(Class<T> preInstanticateBean) {
        final Constructor<T> injectedConstructor = getInjectedConstructor(preInstanticateBean)
                .orElseThrow(() -> new RuntimeException("인스턴스 생성 실패 : 생성자를 찾을 수 없음. class: " + preInstanticateBean.toString()));

        final Object[] parameters = getParameters(injectedConstructor);

        return BeanUtils.instantiateClass(injectedConstructor, parameters);
    }

    private <T> Optional<Constructor<T>> getInjectedConstructor(Class<T> preInstanticateBean) {
        try {
            return Optional.of(preInstanticateBean.getConstructor());
        } catch (NoSuchMethodException e) {
            return Optional.ofNullable((Constructor<T>) BeanFactoryUtils.getInjectedConstructor(preInstanticateBean));
        }
    }

    private Object[] getParameters(Constructor<?> constructor) {
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        return Arrays.stream(parameterTypes)
                .map(t -> BeanFactoryUtils.findConcreteClass(t, preInstanticateBeans))
                .map(this::resolveInstance)
                .toArray();
    }

    private Object resolveInstance(Class<?> parameterType) {
        if (!beans.containsKey(parameterType)) {
            process(parameterType);
        }
        return beans.get(parameterType);
    }

}
