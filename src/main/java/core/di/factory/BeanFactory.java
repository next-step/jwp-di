package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.*;

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
            validateInjectBean();
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("bean register start");
        for (final Class<?> aClass : beans.keySet()) {
            logger.info("bean register : {}", aClass);
        }
    }

    private void validateInjectBean() throws Exception {

        for (final Class<?> clazz : preInstanticateBeans) {
            registerInjectBean(clazz);
        }

        final boolean allMatch = preInstanticateBeans.stream()
                .allMatch(clazz -> Objects.nonNull(getBean(clazz)));
        if (allMatch) {
            return;
        }
        validateInjectBean();
    }

    private void registerInjectBean(Class<?> clazz) throws Exception {
        final Optional<Constructor<?>> optionalConstructor = Arrays.stream(clazz.getDeclaredConstructors())
                .filter(cstr -> cstr.isAnnotationPresent(Inject.class))
                .findAny();

        if (optionalConstructor.isPresent()) {
            final Constructor<?> constructor = optionalConstructor.get();
            List<Class> clazzes = new ArrayList<>();
            for (final Class<?> parameterType : constructor.getParameterTypes()) {
                final Class<?> classByType = getClassByType(parameterType);
                if (Objects.isNull(getBean(classByType))) {
                    return;
                }
                clazzes.add(classByType);
            }

            Object[] objects = clazzes.stream()
                    .map(this::getBean)
                    .toArray();
            beans.put(clazz, constructor.newInstance(objects));
            return;
        }

        beans.put(clazz, clazz.newInstance());
    }

    private Class getClassByType(Class parameterType) {
        if (parameterType.isInterface()) {
            return beans.keySet().stream()
                    .filter(bean -> Arrays.asList(bean.getInterfaces()).contains(parameterType))
                    .findAny()
                    .orElse(parameterType);
        }

        return beans.keySet().stream()
                .filter(bean -> bean.equals(parameterType))
                .findAny()
                .orElse(parameterType);
    }

}
