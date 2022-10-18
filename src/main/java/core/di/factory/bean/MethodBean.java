package core.di.factory.bean;

import org.springframework.beans.factory.BeanCreationException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MethodBean implements Bean {

    private final Method method;

    public MethodBean(Method method) {
        this.method = method;
    }

    @Override
    public Class<?> getType() {
        return method.getReturnType();
    }

    @Override
    public List<Class<?>> getParameterTypes() {
        List<Class<?>> types = new ArrayList<>();
        types.add(method.getDeclaringClass());
        types.addAll(List.of(method.getParameterTypes()));
        return Collections.unmodifiableList(types);
    }

    @Override
    public Object instantiate(List<Object> args) {
        try {
            return method.invoke(args.get(0), othersExcludingFirst(args));
        } catch (Exception e) {
            throw new BeanCreationException(method.getName(), e);
        }
    }

    private Object[] othersExcludingFirst(List<Object> args) {
        return args.stream().skip(1).toArray(Object[]::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodBean that = (MethodBean) o;
        return method.equals(that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method);
    }

    @Override
    public String toString() {
        return "MethodBean{" +
                "method=" + method +
                '}';

    }
}
