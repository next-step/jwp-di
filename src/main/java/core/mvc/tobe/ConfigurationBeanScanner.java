package core.mvc.tobe;

import core.annotation.Bean;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;

public class ConfigurationBeanScanner {

    private final BeanFactory beanFactory;

    public ConfigurationBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void register(Class<?> clazz) {
        this.beanFactory.register(clazz);
//        if (!clazz.isAnnotationPresent(Configuration.class)) {
//            return;
//        }
//
//        List<Method> beanMethods = Arrays.stream(clazz.getDeclaredMethods())
//            .filter(cls -> cls.isAnnotationPresent(Bean.class))
//            .collect(Collectors.toList());
//
//        for (Method beanMethod : beanMethods) {
//            Class<?> beanClazz = beanMethod.getReturnType();
//            Object[] arguments = getArguments(beanMethod);
//
//            try {
//                this.beanFactory.register(beanClazz, beanMethod.invoke(BeanUtils.instantiateClass(clazz), arguments));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

//    private Object[] getArguments(Method method) {
//        List<Object> arguments = new ArrayList<>();
//        Parameter[] parameters = method.getParameters();
//        for (Parameter parameter : parameters) {
//            Object autowireBean = this.beanFactory.getBean(parameter.getType());
//            if (autowireBean == null) {
//                throw new RuntimeException("의존 관계를 주입할 Bean 이 존재하지 않습니다.");
//            }
//            arguments.add(autowireBean);
//        }
//        return arguments.toArray();
//    }

}
