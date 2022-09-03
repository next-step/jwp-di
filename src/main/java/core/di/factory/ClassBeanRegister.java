package core.di.factory;

import com.google.common.collect.Lists;
import core.exception.NoSuchBeanConstructorException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

public class ClassBeanRegister implements BeanRegister {
    private static final int PRIORITY = 1;

    private final Class<?> clazz;
    private Subject subject;

    private Class<?> concreteClass;
    private Constructor<?> constructor;

    @Override
    public void initialize() {
        BeanFactory beanFactory = (BeanFactory) subject;

        concreteClass = BeanFactoryUtils.findConcreteClass(clazz, beanFactory.getInstanticateBeans());
        constructor = findConstructor(concreteClass);
    }

    public ClassBeanRegister(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Class<?> type() {
        return clazz;
    }

    @Override
    public List<Class<?>> interfaces() {
        List<Class<?>> classList = Lists.newArrayList(clazz.getInterfaces());
        classList.add(clazz);

        return classList;
    }

    @Override
    public Object newInstance(Object[] args) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        return constructor.newInstance(args);
    }

    private Constructor<?> findConstructor(Class<?> concreteClass) {
        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(concreteClass);

        if (Objects.nonNull(constructor)) {
            return constructor;
        }

        Constructor<?>[] constructors = concreteClass.getConstructors();

        if (constructors.length > 0) {
            return constructors[0];
        }

        throw new NoSuchBeanConstructorException(concreteClass);
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return constructor.getParameterTypes();
    }

    @Override
    public int getParameterCount() {
        return constructor.getParameterCount();
    }

    @Override
    public int priority() {
        return PRIORITY;
    }

    @Override
    public void subscribe(Subject subject) {
        this.subject = subject;
        initialize();
    }

    @Override
    public void unsubscribe() {
        this.subject = null;
    }
}
