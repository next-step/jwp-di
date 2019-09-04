package core.di.factory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BeanMaker {

    public Map<Class<?>, Object> findAndInitBean(Class<?> clazz, Map<Class<?>, Object> beans) throws BeanMakerException {
        List<Method> methods = BeanFactoryUtils.getBeanMethod(clazz).orElse(new ArrayList<>());

        for(Method method : methods){
            invokeMethod(clazz, method, beans);
        }

        return beans;
    }

    private void invokeMethod(Class<?> clazz, Method method,
                              Map<Class<?>, Object> beans) throws BeanMakerException {

        Class<?>[] parameters = method.getParameterTypes();
        Object[] paramObject = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            paramObject[i] = beans.get(parameters[i]);
        }

        try {
            beans.put(method.getReturnType(), method.invoke(clazz.newInstance(), paramObject));
        }catch (Exception e){
            throw new BeanMakerException(e);
        }
    }

}
