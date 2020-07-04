package core.di.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DefaultBeanFactory implements BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(DefaultBeanFactory.class);

    private final Map<Class<?>, BeanDefinition> definitionMap = Maps.newHashMap();

    private final Map<Class<?>, Object> beans = Maps.newHashMap();

    public DefaultBeanFactory() {
    }

    public DefaultBeanFactory(Set<Class<?>> preInstanticateBeans) {
        // definitionMap = BeanDefinitionUtil.convertClassToDefinition(preInstanticateBeans);
        initialize();
    }

    private void initialize() {
        definitionMap.forEach((clazz, beanDefinition) -> {
            if (!beanDefinition.isLazyInit()) {
                final Object bean = instantiateBean(beanDefinition);
                registerBean(bean, beanDefinition);
            }
        });
    }

    @Override
    public void instantiate() {
        initialize();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        logger.debug("required type: {}", requiredType);
        final Optional<Object> maybeBean = Optional.ofNullable(beans.get(requiredType));
        if (maybeBean.isPresent()) {
            return (T) maybeBean.get();
        }

        final Optional<BeanDefinition> maybeDefinition = Optional.ofNullable(definitionMap.get(requiredType));
        if (maybeDefinition.isEmpty()) {
            return null;
        }

        final BeanDefinition beanDefinition = maybeDefinition.get();
        final Object bean = instantiateBean(beanDefinition);
        registerBean(bean, beanDefinition);
        return (T) bean;
    }

    @Override
    public void registerBeanDefinition(Class<?> clazz, BeanDefinition beanDefinition) {
        logger.debug("registerBeanDefinition - clazz: {}", clazz);
        definitionMap.put(clazz, beanDefinition);
    }

    private Object instantiateBean(BeanDefinition beanDefinition) {
        final List<Class<?>> dependenciesClass = Optional
                .ofNullable(beanDefinition.getDependencies())
                .orElse(Lists.newArrayList());
        final List<Object> dependencies = Lists.newArrayList();
        for (Class<?> clazz : dependenciesClass) {
            dependencies.add(getBean(clazz));
        }

        // logger.debug("bean constructor: {}", beanDefinition.getBeanConstructor());
        // logger.debug("bean dependencies: {}", dependencies);
        return BeanUtils.instantiateClass(beanDefinition.getBeanConstructor(), dependencies.toArray());
    }

    private void registerBean(Object bean, BeanDefinition beanDefinition) {
        final Class<?> originClass = beanDefinition.getOriginalClass();
        beans.put(originClass, bean);
        for (Class<?> type : originClass.getInterfaces()) {
            logger.debug("registerBean - {}", type);
            beans.put(type, bean);
        }
    }
}
