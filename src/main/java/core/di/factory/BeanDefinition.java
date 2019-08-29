package core.di.factory;

import core.annotation.Inject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.StringJoiner;

import static core.di.factory.ReflectionSupport.getArguments;
import static java.util.Optional.ofNullable;
import static org.reflections.ReflectionUtils.getAllConstructors;
import static org.reflections.ReflectionUtils.withAnnotation;
import static org.springframework.beans.BeanUtils.instantiateClass;

public class BeanDefinition {

    private String name;
    private Class<?> type;
    private Object target;
    private Method method;

    public BeanDefinition(Class<?> type) {
        this.type = type;
        this.name = type.getSimpleName();
    }

    public BeanDefinition(Object target, Method method) {
        this.type = method.getReturnType();
        this.name = type.getSimpleName();
        this.target = target;
        this.method = method;
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstance(Class<T> clazz, ArgumentMapper am) {
        if (method != null) {
            Object[] args = ReflectionSupport.getArguments(method.getParameterTypes(), am);
            return (T) ReflectionSupport.invokeMethod(method, target, args);
        }

        return ofNullable(getInjectedConstructor(clazz))
                .map(ctor -> instantiateClass(ctor, getArguments(ctor.getParameterTypes(), am)))
                .orElseGet(() -> instantiateClass(type, clazz));
    }

    /**
     * 인자로 전달하는 클래스의 생성자 중 @Inject 애노테이션이 설정되어 있는 생성자를 반환
     *
     * @Inject 애노테이션이 설정되어 있는 생성자는 클래스당 하나로 가정한다.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private <T> Constructor<T> getInjectedConstructor(Class<T> clazz) {
        Set<Constructor> injectedConstructors = getAllConstructors(clazz, withAnnotation(Inject.class));
        if (injectedConstructors.isEmpty()) {
            return null;
        }
        return injectedConstructors.iterator().next();
    }

    public Class<?> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BeanDefinition that = (BeanDefinition) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(type, that.type)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(type)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BeanDefinition.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("type=" + type)
                .toString();
    }

    public boolean isSame(Class<?> injectedClazz) {
        return type == injectedClazz;
    }

}
