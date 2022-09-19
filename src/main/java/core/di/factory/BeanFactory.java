package core.di.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import core.annotation.web.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private BeanDefinitions beanDefinitions;
    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory() {
    }

    public BeanFactory(BeanDefinitions beanDefinitions) {
        this.beanDefinitions = beanDefinitions;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    /**
     * JdbcQuestionRepository
     * JdbcUserRepository
     * MyQnaService
     * QnaController
     */
    public void register() {
        // @Inject가 설정된 클래스를 클래스타입으로 넣으면 해당 클래스 리턴
        logger.info("bean initialize");

        Map<Class<?>, BeanDefinition> beanDefinitionMap = beanDefinitions.getBeanDefinitions();
        Set<Class<?>> preInstanticateBeans = beanDefinitionMap.keySet();

        for (Class<?> preInstanticateBean : preInstanticateBeans) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(preInstanticateBean);
            beans.put(preInstanticateBean, instantiateBeanDefinition(beanDefinition, preInstanticateBeans));
        }
    }

    private Object instantiateBeanDefinition(BeanDefinition beanDefinition, Set<Class<?>> preInstanticateBeans) {
        Class<?>[] parameterTypes = beanDefinition.parameterTypes();
        List<Object> objects = Lists.newArrayList();
        for (int i = 0; i < parameterTypes.length; i++) {
            Object parameter = getParametersOfBeanDefinition(parameterTypes[i], preInstanticateBeans);
            objects.add(parameter);
        }
        return instantiateBean(objects.toArray(), beanDefinition);
    }

    private Object instantiateBean(Object[] parameters, BeanDefinition beanDefinition) {
        return beanDefinition.instantiate(parameters);
    }

    private Object getParametersOfBeanDefinition(Class<?> parameterType, Set<Class<?>> preInstanticateBeans) {
        if (beanDefinitions.get(parameterType) == null) {
            Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(parameterType, preInstanticateBeans);
            Object parameter = instantiateBeanDefinition(beanDefinitions.get(concreteClass), preInstanticateBeans);
            beans.put(parameterType, parameter);
            return parameter;
        }
        Object parameter = instantiateBeanDefinition(beanDefinitions.get(parameterType), preInstanticateBeans);
        beans.put(parameterType, parameter);
        return parameter;
    }
    public Map<Class<?>, Object> getControllers() {
        return beans.entrySet()
                .stream()
                .filter(entry -> entry.getKey().isAnnotationPresent(Controller.class))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
