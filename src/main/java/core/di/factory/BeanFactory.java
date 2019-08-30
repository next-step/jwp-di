package core.di.factory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import core.annotation.web.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstanticateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public void initInstanticateBeans(Set<Class<?>> preInstanticateBeans){
        this.preInstanticateBeans = preInstanticateBeans;
    }

    public void initInstanticateBeans(Class<?> preInstanticateBean){
        preInstanticateBeans = Sets.newHashSet();
        preInstanticateBeans.add(preInstanticateBean);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        try {
            InjectMaker injectMaker = new InjectMaker(preInstanticateBeans);
            BeanMaker beanMaker = new BeanMaker();

            for (Class<?> clazz : preInstanticateBeans) {
                addBean(clazz, injectMaker.findAndInitInject(clazz));
                addBeans(beanMaker.findAndInitBean(clazz, beans));
            }
        }catch (Exception e){
            logger.error("Bean Create Initialize Error {}", e.getMessage());
        }
    }

    public Map<Class<?>, Object> getControllers() {
        Map<Class<?>, Object> controllers = Maps.newHashMap();

        List<Class<?>> controllerList = preInstanticateBeans.stream()
                .filter(clazz -> clazz.isAnnotationPresent(Controller.class))
                .collect(Collectors.toList());

        for (Class<?> clazz : controllerList){
            controllers.put(clazz, beans.get(clazz));
        }

        return controllers;
    }

    private void addBean(Class<?> clazz, Object value){
        if(!beans.containsKey(clazz)){
            beans.put(clazz, value);
            return;
        }

        if(beans.get(clazz) == null){
            beans.put(clazz, value);
        }
    }

    private void addBeans(Map<Class<?>, Object> lists){
        for(Class<?> clazz : lists.keySet()){
            addBean(clazz, beans.get(clazz));
        }
    }

}
