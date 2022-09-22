package core.di;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanDefinitions {
    private Set<BeanDefinition> beanDefinitions;

    public BeanDefinitions() {
        this.beanDefinitions = new HashSet<>();
    }

    public void add(BeanDefinition beanDefinition) {
        beanDefinitions.add(beanDefinition);
    }

    public BeanDefinition getMethodBeanDefinition(Class<?> methodReturnType) {
        return beanDefinitions.stream().filter(beanDefinition -> beanDefinition.isMethodReturnTypeEqual(methodReturnType))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 메서드 빈 정보는 등록되어 있지 않습니다."));
    }

    public Set<BeanDefinition> getBeanDefinitions() {
        return this.beanDefinitions;
    }

    public Set<Class<?>> getPreInstantiateClassBean() {
        return this.beanDefinitions.stream().map(BeanDefinition::getBeanClass).collect(Collectors.toSet());
    }
}
