package core.di.factory;

/**
 * Created By kjs4395 on 7/20/20
 */
public interface BeanMaker {
     boolean isSupport(BeanInfo beanInfo);

     <T> T makeBean(BeanInfo beanInfo,BeanFactory2 beanFactory);

}
