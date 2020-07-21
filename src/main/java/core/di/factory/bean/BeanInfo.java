package core.di.factory.bean;

import core.di.factory.bean.BeanInvokeType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created By kjs4395 on 7/20/20
 */
public class BeanInfo {

    private final Class<?> returnType;
    private final Class<?> defineClazz;
    private final BeanInvokeType beanInvokeType;
    private final Constructor constructor ;
    private final Method method;

    public BeanInfo(Class<?> returnType, Class<?> defineClazz, BeanInvokeType beanInvokeType, Constructor constRuctor, Method method) {
        this.returnType = returnType;
        this.defineClazz = defineClazz;
        this.beanInvokeType = beanInvokeType;
        this.constructor = constRuctor;
        this.method = method;
    }

    public BeanInvokeType getBeanInvokeType() {
        return beanInvokeType;
    }

    public Constructor getConstructor() {
        return constructor;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public Class<?> getDefineClazz() {
        return defineClazz;
    }

}
