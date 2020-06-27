package core.di.factory;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class BeanFactory {
    private Set<Class<?>> preInstanticateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();
    private List<BeanGetter> beanGetters;

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;

        preInstanticateBeans.forEach(
            clazz -> log.debug("preInstanticateBeans: {}", clazz.getSimpleName())
        );

        this.beanGetters = Arrays.asList(new ConstructorBeanGetter(preInstanticateBeans, beans));
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        for (Class<?> beanClass : preInstanticateBeans) {
            for (BeanGetter beanGetter : beanGetters) {
                beans.put(beanClass, beanGetter.getBean(beanClass));
            }
        }

        beans.keySet().forEach(
            clazz -> log.debug("beanClassName: {}", clazz.getSimpleName())
        );
    }
}

