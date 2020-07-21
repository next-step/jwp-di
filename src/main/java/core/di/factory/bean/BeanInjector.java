package core.di.factory.bean;


import core.di.factory.BeanFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By kjs4395 on 2020-07-20
 */
public class BeanInjector {

    public static List<BeanMaker> beanMakers = new ArrayList<>();

    static {
        beanMakers.add(new ConstructorBeanMaker());
        beanMakers.add(new MethodBeanMaker());
    }

    public static <T> T injectBean(BeanInfo beanInfo, BeanFactory beanFactory) {
        return beanMakers.stream()
                .filter(beanMaker -> beanMaker.isSupport(beanInfo))
                .findFirst()
                .orElseThrow(IllegalAccessError::new)
                .makeBean(beanInfo,beanFactory);
    }
}
