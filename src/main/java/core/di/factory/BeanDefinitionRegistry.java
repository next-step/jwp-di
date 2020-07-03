package core.di.factory;

import jdk.internal.jline.internal.Nullable;

import java.util.Set;

/**
 * @author KingCjy
 */
public interface BeanDefinitionRegistry {
    void registerDefinition(BeanDefinition beanDefinition);

    @Nullable
    BeanDefinition getBeanDefinition(String name);

    Set<BeanDefinition> getBeanDefinitions(Class<?> type);
}
