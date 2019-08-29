package core.di.factory.config;

public interface BeanCreator {

    Object create(Object[] params) throws Exception;
}
