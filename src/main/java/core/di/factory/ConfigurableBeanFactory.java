package core.di.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import core.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static java.util.stream.Collectors.toMap;

@Slf4j
public class ConfigurableBeanFactory {
    public static final Class CONFIGURATION_CLASS = Configuration.class;

    private final Set<Class<?>> preInstanticateBeans;
    private final List<BeanDefinitionResolver> beanDefinitionResolvers;
    private final Map<Class<?>, Object> beans = Maps.newHashMap();
    private final Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();

    public ConfigurableBeanFactory(Set<Class<?>> preInstantiatedBeans) {
        this.preInstanticateBeans = preInstantiatedBeans;
        this.beanDefinitionResolvers = Arrays.asList(new MethodBeanDefinitionResolver(preInstantiatedBeans, beanDefinitions));
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.getOrDefault(requiredType, getBeanFromDefinition(requiredType));
    }

    public void initialize() {
        for (Class<?> beanClass : preInstanticateBeans) {
            for (BeanDefinitionResolver beanDefinitionResolver : beanDefinitionResolvers) {
                beanDefinitions.put(beanClass, beanDefinitionResolver.resolve(beanClass));
            }
        }

        beanDefinitions.values().forEach(
            beanDefinition -> log.debug("beanDefinitions: {}, {}", beanDefinition.getType().getSimpleName(), beanDefinition.getAnnotations())
        );
    }

    private <T> Object getBeanFromDefinition(Class<T> requiredType) {
        BeanDefinition beanDefinition = beanDefinitions.get(requiredType);

        if (Objects.isNull(beanDefinition)) {
            return null;
        }

        if (Objects.isNull(beanDefinition.getConstructor())) {
            return BeanUtils.instantiateClass(beanDefinition.getType());
        }

        if (CollectionUtils.isEmpty(beanDefinition.getChildren())) {
            return BeanUtils.instantiateClass(beanDefinition.getConstructor());
        }

        return getParameterizedBean(beanDefinition);
    }

    private Object getParameterizedBean(BeanDefinition beanDefinition) {
        List<Object> parameters = Lists.newArrayList();

        for (BeanDefinition child : beanDefinition.getChildren()) {
            if (beans.containsKey(child.getType())) {
                parameters.add(beans.get(child.getType()));
            }
            else {
                Object beanInstance = getBean(child.getType());
                beans.put(child.getType(), beanInstance);
                parameters.add(beanInstance);
            }
        }

        return BeanUtils.instantiateClass(beanDefinition.getConstructor(), parameters.toArray(new Object[0]));
    }


    public Map<Class<?>, Object> getControllers() {
        return beanDefinitions.entrySet()
            .stream()
            .filter(entry -> entry.getValue().getAnnotations().contains(CONFIGURATION_CLASS))
            .collect(toMap(Map.Entry::getKey, entry -> getBean(entry.getKey())));
    }
}