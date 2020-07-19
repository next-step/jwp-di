package core.di.factory;


import java.util.List;

/**
 * Created By kjs4395 on 2020-07-20
 */
public class BeanInjector {

    public static List<BeanMaker> beanMakers;

    static {
        beanMakers.add(new ConstructorBeanMaker());
        beanMakers.add(new MethodBeanMaker());
    }

    public static <T> T injectBean(BeanInfo beanInfo, BeanFactory2 beanFactory) {
        return beanMakers.stream()
                .filter(beanMaker -> beanMaker.isSupport(beanInfo))
                .findFirst()
                .orElseThrow(IllegalAccessError::new)
                .makeBean(beanInfo,beanFactory);
    }
}
