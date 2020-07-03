package core.di.beans.definition.reader;

import core.di.beans.definition.AnnotatedBeanDefinition;
import core.di.beans.definition.BeanDefinitionReader;
import core.di.beans.definition.BeanDefinitionRegistry;
import core.di.beans.definition.DefaultBeanDefinition;
import core.di.factory.BeanFactoryUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Set;

@Slf4j
public class AnnotatedBeanDefinitionReader implements BeanDefinitionReader {
    private BeanDefinitionRegistry beanDefinitionRegistry;

    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    @Override
    public void read(Class<?>... types) {
        for (Class<?> type : types) {
            registerBeanDefinition(type);
        }
    }

    private void registerBeanDefinition(Class<?> type) {
        beanDefinitionRegistry.register(type, new DefaultBeanDefinition(type));
        Set<Method> beanMethods = BeanFactoryUtils.getBeanMethods(type);

        for (Method beanMethod : beanMethods) {
            log.debug("@Bean method : {}", beanMethod);
            AnnotatedBeanDefinition beanDefinition = new AnnotatedBeanDefinition(beanMethod.getReturnType(), beanMethod);
            beanDefinitionRegistry.register(beanMethod.getReturnType(), beanDefinition);
        }
    }
}
