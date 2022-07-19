package core.di.factory;

import com.google.common.collect.Maps;
import core.annotation.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstanticateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        initIndependentBeans();

        int initializedBeanCount = beans.size();
        while (!beans.keySet().containsAll(preInstanticateBeans)) {
            preInstanticateBeans.stream()
                                .filter(bean -> !beans.containsKey(bean))
                                .forEach(bean -> {
                                    Arrays.stream(bean.getDeclaredConstructors())
                                          .filter(constructor -> !beans.containsKey(bean) && constructor.isAnnotationPresent(Inject.class))
                                          .forEach(constructor -> {
                                              Class<?>[] parameterTypes = constructor.getParameterTypes();
                                              Set<Class<?>> classes = beans.keySet();
                                              if (classes.containsAll(Set.of(parameterTypes))) {
                                                  List<Object> objects = Arrays.stream(parameterTypes)
                                                                               .map(parameterType -> beans.get(parameterType))
                                                                               .collect(Collectors.toList());
                                                  Object o = BeanUtils.instantiateClass(constructor, objects.toArray());
                                                  Arrays.stream(bean.getInterfaces()).forEach(aClass -> beans.put(aClass, o));
                                                  beans.put(bean, o);
                                              }
                                          });
            });
            if (initializedBeanCount == beans.size()) {
                throw new IllegalArgumentException("생성 불가능한 빈이 존재합니다.");
            }
            initializedBeanCount = beans.size();
        }
    }

    private void initIndependentBeans() {
        preInstanticateBeans.forEach(bean -> {
            Arrays.stream(bean.getDeclaredConstructors())
                  .filter(method -> !beans.containsKey(bean) && !method.isAnnotationPresent(Inject.class))
                  .forEach(__ -> {
                      Object o = BeanUtils.instantiateClass(bean);
                      Arrays.stream(bean.getInterfaces()).forEach(aClass -> beans.put(aClass, o));
                      beans.put(bean, o);
                  });
        });
    }
}
