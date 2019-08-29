package core.di.factory;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BeanDefinitions {

    private Set<BeanDefinition> beanDefinitions;

    public BeanDefinitions() {
        this.beanDefinitions = new HashSet<>();
    }

    public void register(BeanDefinition bd) {
        if (beanDefinitions.contains(bd)) {
            throw new IllegalArgumentException(bd.getName() + " already exist");
        }

        beanDefinitions.add(bd);
    }

    /**
     * 인터페이스인 경우 BeanFactory가 관리하는 모든 클래스 중에 인터페이스를 구현하는 클래스를 찾아 반환
     * 인자로 전달되는 클래스의 구현 클래스. 만약 인자로 전달되는 Class가 인터페이스가 아니면 전달되는 인자가 구현 클래스,
     */
    public Optional<BeanDefinition> findBeanDefinition(Class<?> injectedClazz) {
        for (BeanDefinition bd : this.beanDefinitions) {
            if (!injectedClazz.isInterface() && bd.isSame(injectedClazz)) {
                return Optional.of(bd);
            }

            Class<?> clazz = bd.getType();
            Set<Class<?>> interfaces = Sets.newHashSet(clazz.getInterfaces());
            if (interfaces.contains(injectedClazz) || clazz == injectedClazz) {
                return Optional.of(bd);
            }
        }

        return Optional.empty();
    }

    public Set<BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }
}
