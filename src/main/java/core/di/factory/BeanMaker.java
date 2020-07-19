package core.di.factory;

/**
 * Created By kjs4395 on 7/20/20
 */
public interface BeanMaker {
    public boolean isSupport(BeanInfo beanInfo);
    public <T> T makeBean(BeanInfo beanInfo);
}
