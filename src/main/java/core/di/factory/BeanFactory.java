package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstanticateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        try {
            applyNoneInjectClass();
            registerInjectBean();
            validateInjectBean();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validateInjectBean() throws Exception {
        for (final Class<?> preInstanticateBean : preInstanticateBeans) {
            final boolean match = Arrays.stream(preInstanticateBean.getDeclaredConstructors())
                    .filter(constructor -> constructor.isAnnotationPresent(Inject.class))
                    .allMatch(clazz -> Objects.nonNull(getBean(preInstanticateBean)));
            if (!match) {
                registerInjectBean();
            }
        }
    }

    private void registerInjectBean() throws Exception {
        for (final Class<?> preInstanticateBean : preInstanticateBeans) {
            for (final Constructor<?> declaredConstructor : preInstanticateBean.getDeclaredConstructors()) {
                final boolean annotationPresent = declaredConstructor.isAnnotationPresent(Inject.class);
                if (annotationPresent) {

                    Object[] objects = new Object[declaredConstructor.getParameterTypes().length];

                    final boolean match = Arrays.stream(declaredConstructor.getParameterTypes())
                            .allMatch(type -> Objects.nonNull(getBean(type)));

                    if (match) {
                        final Class<?>[] parameterTypes = declaredConstructor.getParameterTypes();
                        for (int i = 0; i < parameterTypes.length; i++) {
                            objects[i] = getBean(parameterTypes[i]);
                        }
                        beans.put(preInstanticateBean, declaredConstructor.newInstance(objects));
                    }
                }
            }
        }
    }

    private void applyNoneInjectClass() throws Exception {
        for (final Class<?> clazz : preInstanticateBeans) {
            final boolean anyMatch = Arrays.stream(clazz.getDeclaredConstructors())
                    .anyMatch(constructor -> constructor.isAnnotationPresent(Inject.class));

            if (!anyMatch) {
                for (final Class<?> anInterface : clazz.getInterfaces()) {
                    beans.put(anInterface, clazz.newInstance());
                }
                beans.put(clazz, clazz.newInstance());
            }
        }
    }

}
