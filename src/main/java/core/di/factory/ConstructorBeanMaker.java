package core.di.factory;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By kjs4395 on 7/20/20
 */
public class ConstructorBeanMaker implements BeanMaker{


    @Override
    public boolean isSupport(BeanInfo beanInfo) {
        return beanInfo.getBeanInvokeType().equals(BeanInvokeType.CONSTRUCTOR);
    }

    @Override
    public <T> T makeBean(BeanInfo beanInfo, BeanFactory2 beanFactory) {
        Class<?> findClass = beanInfo.getReturnType();

        if(BeanFactoryUtils.getInjectedConstructor(findClass) == null) {
            return (T) BeanUtils.instantiateClass(findClass);
        }

        Constructor constructor = beanInfo.getConstructor();
        List<Object> constructorValues = new ArrayList<>();

        for(Class<?> parameter : constructor.getParameterTypes()) {
            constructorValues.add(beanFactory.getBean(parameter));
        }

        return (T) BeanUtils.instantiateClass(constructor, constructorValues.toArray());
    }
}
