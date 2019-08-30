package core.di.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.Set;

public class InjectMaker {
    private static final Logger logger = LoggerFactory.getLogger(InjectMaker.class);

    private Set<Class<?>> preInstanticateBeans;

    public InjectMaker(Set<Class<?>> preInstanticateBeans){
        this.preInstanticateBeans = preInstanticateBeans;
    }

    public Optional<Object> findAndInitInject(Class<?> clazz) {
        Object returnObject = null;
        try {
            clazz = BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans);
            returnObject = getNewInstance(clazz, BeanFactoryUtils.getInjectedConstructor(clazz));
        } catch (Exception e) {
            logger.error("injection Error {}", e.getMessage());
        }

        return Optional.ofNullable(returnObject);
    }

    private Object getNewInstance(Class<?> clazz, Constructor constructor) throws Exception {
        return (constructor == null)
                ? clazz.newInstance()
                : constructor.newInstance(getConstructParameter(
                constructor.getParameterTypes()));
    }

    private Object[] getConstructParameter(Object[] parameterObject) {
        Object[] paramList = new Object[parameterObject.length];

        for (int i = 0; i < parameterObject.length; i++) {
            paramList[i] = findAndInitInject((Class<?>) parameterObject[i]);
        }

        return paramList;
    }
}
