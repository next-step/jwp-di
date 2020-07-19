package core.di.factory;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Set;

/**
 * Created By kjs4395 on 7/20/20
 */
public class BeanFactory2 {
    private Set<Class<?>> preInstanticateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

}
