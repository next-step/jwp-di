package core.di;

import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Objects;

public class BeanDefinition {
    private Class<?> beanClass;
    private Method beanMethod;

    public BeanDefinition(Class<?> beanClass, Method beanMethod) {
        Assert.notNull(beanClass, "빈 클래스 정보가 null 일 수 없습니다.");
        Assert.notNull(beanMethod, "빈 메서드 정보는 null 일 수 없습니다.");
        this.beanClass = beanClass;
        this.beanMethod = beanMethod;
    }

    public BeanDefinition(Class<?> beanClass) {
        Assert.notNull(beanClass, "빈 클래스 정보가 null 일 수 없습니다.");
        this.beanClass = beanClass;
    }

    public Method getBeanMethod() {
        return this.beanMethod;
    }

    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    public Class<?> methodReturnType() {
        return this.beanMethod.getReturnType();
    }

    public boolean isMethodReturnTypeEqual(Class<?> methodReturnType) {
        Assert.notNull(this.beanMethod, "해당 빈의 정보는 클래스 타입 빈입니다. 메서드 타입 빈과 비교할 수 없습니다.");
        return methodReturnType() == methodReturnType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeanDefinition that = (BeanDefinition) o;

        if (!beanClass.equals(that.beanClass)) return false;
        return Objects.equals(beanMethod, that.beanMethod);
    }

    @Override
    public int hashCode() {
        int result = beanClass.hashCode();
        result = 31 * result + (beanMethod != null ? beanMethod.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "beanClass=" + beanClass +
                ", beanMethod=" + beanMethod +
                '}';
    }
}
