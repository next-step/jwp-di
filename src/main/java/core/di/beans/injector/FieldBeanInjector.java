package core.di.beans.injector;

import core.di.beans.definition.BeanDefinition;
import core.di.beans.getter.BeanGettable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.Set;

@Slf4j
public class FieldBeanInjector implements BeanInjector {
    @Override
    public <T> T inject(BeanGettable beanGettable, BeanDefinition beanDefinition) {
        T bean = (T) BeanUtils.instantiateClass(beanDefinition.getType());

        Set<Field> injectFields = beanDefinition.getFields();
        for (Field field : injectFields) {
            injectField(beanGettable, bean, field);
        }
        return bean;
    }

    private void injectField(BeanGettable beanGettable, Object bean, Field field) {
        log.debug("Inject Bean : {}, Field : {}", bean, field);

        try {
            field.setAccessible(true);
            field.set(bean, beanGettable.getBean(field.getType()));
        }
        catch (IllegalAccessException | IllegalArgumentException e) {
            log.error(e.getMessage());
        }
    }

}
