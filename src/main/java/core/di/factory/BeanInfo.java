package core.di.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created By kjs4395 on 7/20/20
 */
public class BeanInfo {
    private Class<?> returnType;
    private Class<?> defineClazz;
    private BeanInvokeType beanInvokeType;
    private Constructor constructor ;
    private Method method;

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
