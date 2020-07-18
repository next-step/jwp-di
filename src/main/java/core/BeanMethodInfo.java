package core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Created By kjs4395 on 7/18/20
 */
public class BeanMethodInfo {
    private final Method method;
    private final Object config;
    private final Class<?> returnType;

    public BeanMethodInfo(Method method, Object config, Class<?> returnType) {
        this.method = method;
        this.config = config;
        this.returnType = returnType;
    }

    public Method getMethod() {
        return method;
    }

    public Object getConfig() {
        return config;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    /**
     * 파라미터가 없는지 체크
     * @return
     */
    public boolean hasNoParameter() {
        return this.method.getParameters().length == 0;
    }

    public <T> T methodInvoke(Object[] parameterValues) {
        try {
            return (T) method.invoke(config, parameterValues);
        } catch (IllegalAccessException|InvocationTargetException e) {
           throw new IllegalArgumentException("method invoke fail!");
        }
    }

    public Parameter[] getParameters() {
        return method.getParameters();
    }
}
