package core.di.factory.constructor;

import core.annotation.Bean;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MethodBeanConstructor implements BeanConstructor {

    private final Method method;

    public MethodBeanConstructor(Method method) {
        Assert.notNull(method, "method가 null이어선 안됩니다.");
        Assert.isTrue(method.isAnnotationPresent(Bean.class), "method는 Bean 어노테이션이 설정되어 있어야 합니다.");
        this.method = method;
    }


    @Override
    public Class<?> type() {
        return method.getReturnType();
    }

    @Override
    public List<Class<?>> parameterTypes() {
        List<Class<?>> types = new ArrayList<>();
        types.add(method.getDeclaringClass());
        types.addAll(List.of(method.getParameterTypes()));
        return Collections.unmodifiableList(types);
    }

    @Override
    public Object instantiate(List<Object> args) {
        validateArgs(args);
        try {
            return method.invoke(args.get(0), othersExcludingFirst(args));
        } catch (Exception e) {
            throw new BeanCreationException(method.getName(), e);
        }
    }

    private Object[] othersExcludingFirst(List<Object> args) {
        return args.stream()
                .skip(1)
                .toArray(Object[]::new);
    }

    private void validateArgs(List<Object> args) {
        if (args == null || args.isEmpty() || doesNotHaveMethod(args.get(0))) {
            throw new IllegalStateException(
                    String.format("args (%s)는 method (%s)의 객체를 갖고 있어야 합니다.", args, method));
        }
    }

    private boolean doesNotHaveMethod(Object target) {
        try {
            target.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
            return false;
        } catch (NoSuchMethodException e) {
            return true;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodBeanConstructor that = (MethodBeanConstructor) o;
        return Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method);
    }

    @Override
    public String toString() {
        return "MethodBeanConstructor{" +
                "method=" + method +
                '}';
    }
}
