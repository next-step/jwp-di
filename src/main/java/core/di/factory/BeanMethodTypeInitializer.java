package core.di.factory;

import com.google.common.collect.ImmutableList;
import core.annotation.Bean;

import java.lang.reflect.Method;
import java.util.List;

public class BeanMethodTypeInitializer implements BeanInitializer {

    private final BeanFactory beanFactory;
    private final List<BeanMethodInitializer> initializers;

    public BeanMethodTypeInitializer(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        initializers = ImmutableList.of(
                new NoneParameterBeanMethodInitializer(), new MultiParameterBeanMethodInitializer(beanFactory)
        );
    }

    @Override
    public boolean support(Object type) {
        return type instanceof Method && ((Method) type).isAnnotationPresent(Bean.class);
    }

    @Override
    public Object initialize(BeanRegistry beanRegistry, Object type) {
        Method method = (Method) type;
        return initializers.stream()
                .filter(initializer -> initializer.support(method))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("지원 되지 않는 타입입니다. type : [ " + method + "]"))
                .initialize(beanRegistry, method);
    }
}
