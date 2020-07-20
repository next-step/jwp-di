package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.web.Controller;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created By kjs4395 on 7/20/20
 */
public class BeanFactory2 {

    private Map<Class<?>,BeanInfo> registerBeanInfos = Maps.newHashMap();

    private Map<Class<?>, Object> beans = Maps.newHashMap();



    public void initialize() {
        registerBeanInfos.keySet()
                .forEach(findBean -> getBean(findBean));
    }

    public void register(Set<BeanInfo> beanInfos) {
        beanInfos.forEach(beanInfo -> {
            this.registerBeanInfos.put(beanInfo.getReturnType(), beanInfo);
        });
    }

    public <T> T getBean(Class<T> requiredType) {
        if (beans.containsKey(requiredType)) {
            return (T) beans.get(requiredType);
        }

        return (T) makeBean(requiredType);
    }

    private <T> T makeBean(Class<T> requiredType) {
        T bean =  BeanInjector.injectBean(registerBeanInfos.get(requiredType),this);
        beans.put(requiredType, bean);

        return bean;
    }

    public Set<Object> controllers() {
        return this.registerBeanInfos.keySet()
                        .stream()
                        .filter(clazz -> clazz.isAnnotationPresent(Controller.class))
                        .map(clazz -> {return getBean(clazz);})
                        .collect(Collectors.toSet());
    }

}
