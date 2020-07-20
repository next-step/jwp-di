package core.di.factory.bean;

import core.di.factory.BeanFactory;

/**
 * Created By kjs4395 on 7/20/20
 */
public interface BeanMaker {
     boolean isSupport(BeanInfo beanInfo);

     <T> T makeBean(BeanInfo beanInfo, BeanFactory beanFactory);

}
