package core.di.factory;

public interface BeanInitializer {

    boolean support(Object type);

    Object initialize(BeanRegistry beanRegistry, Object type);

}
