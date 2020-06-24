package core.di.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class BeanFactory {
    private Set<Class<?>> preInstanticateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
        log.debug("preInstanticateBeans: {}", preInstanticateBeans);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        for (Class<?> beanClass : preInstanticateBeans) {
            Set<Constructor> injectedConstructors = BeanFactoryUtils.getInjectedConstructors(beanClass);

            for (Constructor constructor : injectedConstructors) {
                List<Object> arguments = Lists.newArrayList();
                for (Parameter parameter : constructor.getParameters()) {
                    Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(parameter.getType(), preInstanticateBeans);
                    if (beans.containsKey(concreteClass)) {
                        arguments.add(beans.get(concreteClass));
                    }
                    else {

                    }
                }

            }
            log.debug("injectedConstructors: {}", injectedConstructors);
        }
    }
}

