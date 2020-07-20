package core.di;

import static core.di.BeansUtils.getParameterObjects;

import com.google.common.collect.Maps;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

/**
 * Created by iltaek on 2020/07/17 Blog : http://blog.iltaek.me Github : http://github.com/iltaek
 */
public class Beans {

    private static final Logger logger = LoggerFactory.getLogger(Beans.class);
    private final Map<Class<?>, Object> beans = Maps.newHashMap();
    private BeanDefinitions beanDefinitions;

    public void instantiateBeans(BeanDefinitions beanDefinitions) {
        this.beanDefinitions = beanDefinitions;
        beanDefinitions.getBeanDefinitionMap()
                       .forEach((clazz, beandef) -> beans.put(clazz, instantiate(beandef)));
    }

    public Object get(Class<?> beanClass) {
        return beans.get(beanClass);
    }

    private Object instantiate(BeanDefinition beandef) {
        if (beandef instanceof ClasspathBeanDefinition) {
            return instantiateClasspathBean(beandef);
        }
        return instantiateConfigBean(beandef);
    }

    private Object instantiateConfigBean(BeanDefinition beandef) {
        if (isPresent(beandef.getBeanClass())) {
            return beans.get(beandef.getBeanClass());
        }

        Method beanMethod = beandef.getMethod();
        List<Object> params = getParameterObjects(beanMethod.getParameterTypes(),
                                                  parameterType -> instantiateConfigBean(beanDefinitions.getConfigBeanDefinition(parameterType)));
        return putAndGet(beanMethod.getReturnType(), instantiateWithMethod(beanMethod, params.toArray()));
    }

    private Object instantiateWithMethod(Method method, Object[] params) {
        Object configBean = instantiateClasspathBean(beanDefinitions.getConfigBeanDefinition(method.getDeclaringClass()));
        try {
            return method.invoke(configBean, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private Object instantiateClasspathBean(BeanDefinition beandef) {
        if (isPresent(beandef.getBeanClass())) {
            return beans.get(beandef.getBeanClass());
        }

        if (beandef.getBeanConstructor() == null) {
            logger.debug("Instantiate {} class", beandef.getBeanClass().getName());
            return putAndGet(beandef.getBeanClass(), instantiateBean(beandef));
        }

        logger.debug("Instantiate {} class", beandef.getBeanClass().getName());
        return putAndGet(beandef.getBeanClass(), instantiateConstructor(beandef.getBeanConstructor()));
    }

    private Object instantiateConstructor(Constructor<?> constructor) {
        List<Object> params = getParameterObjects(constructor.getParameterTypes(),
                                                  parameterType -> instantiate(beanDefinitions.getConcreteBeanDefinition(parameterType)));
        return BeanUtils.instantiateClass(constructor,
                                          params.toArray());
    }

    private Object instantiateBean(BeanDefinition beandef) {
        return BeanUtils.instantiateClass(beanDefinitions.getConcreteClass(beandef.getBeanClass()));
    }

    private boolean isPresent(Class<?> beanClass) {
        return beans.containsKey(beanClass);
    }

    private Object putAndGet(Class<?> beanClass, Object bean) {
        beans.put(beanClass, bean);
        return beans.get(beanClass);
    }
}
