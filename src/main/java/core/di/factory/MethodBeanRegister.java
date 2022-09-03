package core.di.factory;

import com.google.common.collect.Lists;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public class MethodBeanRegister implements BeanRegister {
    private static final int PRIORITY = 2;
    private final Method method;

    private Subject subject;

    public MethodBeanRegister(Method method) {
        this.method = method;
    }

    @Override
    public void initialize() {
        BeanFactory beanFactory = (BeanFactory) subject;

        Object configuration = beanFactory.getBean(method.getDeclaringClass());
        if (Objects.isNull(configuration)) {
            beanFactory.register(new ClassBeanRegister(method.getDeclaringClass()));
        }
    }

    @Override
    public Class<?> type() {
        return method.getReturnType();
    }

    @Override
    public List<Class<?>> interfaces() {
        List<Class<?>> classList = Lists.newArrayList(method.getReturnType().getInterfaces());
        classList.add(method.getReturnType());

        return classList;
    }

    @Override
    public Object newInstance(Object[] args) throws IllegalAccessException, InvocationTargetException {
        BeanFactory beanFactory = (BeanFactory) subject;

        return method.invoke(beanFactory.getBean(method.getDeclaringClass()), args);
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return method.getParameterTypes();
    }

    @Override
    public int getParameterCount() {
        return method.getParameterCount();
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
