package core.di;

import org.springframework.util.Assert;

import java.lang.reflect.Method;

public class MethodBeanDefinition implements BeanDefinition {
    private Class<?> beanClass;
    private Method beanMethod;

    public MethodBeanDefinition(Class<?> beanClass, Method beanMethod) {
        Assert.notNull(beanClass, "빈 클래스 정보가 null 일 수 없습니다.");
        Assert.notNull(beanMethod, "빈 메서드 정보는 null 일 수 없습니다.");
        this.beanClass = beanClass;
        this.beanMethod = beanMethod;
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

    public Boolean isMethodReturnTypeEqual(Class<?> methodReturnType) {
        return methodReturnType() == methodReturnType;
    }
}
