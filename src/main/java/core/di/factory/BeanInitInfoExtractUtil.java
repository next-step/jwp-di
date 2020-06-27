package core.di.factory;

import core.annotation.Bean;
import core.annotation.Configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BeanInitInfoExtractUtil {

    private BeanInitInfoExtractUtil() {}

    public static Map<Class<?>, BeanInitInfo> extractBeanInitInfo(Class<?> clazz) {
        Map<Class<?>, BeanInitInfo> beanInitInfos = new HashMap<>();

        if (clazz.isAnnotationPresent(Configuration.class)) {
            beanInitInfos.putAll(extractBeans(clazz));
        }

        BeanType beanType = extractBeanType(clazz);
        if (beanType != null) {
            beanInitInfos.put(clazz, new BeanInitInfo(clazz, beanType));
        }

        return beanInitInfos;
    }

    private static BeanType extractBeanType(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredAnnotations())
                .map(Annotation::annotationType)
                .filter(ComponentScanner.targetAnnotations::contains)
                .findFirst()
                .map(BeanType::of)
                .orElse(null);
    }

    private static Map<Class<?>, BeanInitInfo> extractBeans(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .map(method -> methodToBeanInitInfo(clazz, method))
                .collect(Collectors.toMap(BeanInitInfo::getClassType, Function.identity()));
    }

    private static BeanInitInfo methodToBeanInitInfo(Class<?> clazz, Method method) {
        Class<?> classType = method.getReturnType();

        return new BeanInitInfo(classType, new MethodInfo(clazz, method), BeanType.BEAN);
    }
}
