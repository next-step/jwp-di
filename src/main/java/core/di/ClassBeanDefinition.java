package core.di;

import org.springframework.util.Assert;

import java.lang.reflect.Method;

public class ClassBeanDefinition implements BeanDefinition {
    private final Class<?> beanClass;

    public ClassBeanDefinition(Class<?> beanClass) {
        Assert.notNull(beanClass, "빈 클래스 정보가 null 일 수 없습니다.");
        this.beanClass = beanClass;
    }

    public Method getBeanMethod() {
        throw new IllegalArgumentException("해당 빈의 정보는 클래스 타입 빈입니다. 메서드 타입 빈 정보를 가져올 수 없습니다.");
    }

    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    public Class<?> methodReturnType() {
        throw new IllegalArgumentException("해당 빈의 정보는 클래스 타입 빈입니다. 메서드 타입 빈 정보를 가져올 수 없습니다.");

    }

    public Boolean isMethodReturnTypeEqual(Class<?> methodReturnType) {
        throw new IllegalArgumentException("해당 빈의 정보는 클래스 타입 빈입니다. 메서드 타입 빈과 비교할 수 없습니다.");
    }
}
