package core.di.factory;

import java.util.HashSet;
import java.util.Set;

public class PreInstantiateBeans {

    public Object createBeanObject(Class<?> clazz) {
        try {
            Set<Class<?>> preInstantiateBeans = new HashSet<>();
            preInstantiateBeans.add(clazz);

            Class conClass = BeanFactoryUtils.findConcreteClass(clazz, preInstantiateBeans);
            return conClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
