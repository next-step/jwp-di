package core.di.bean;

public interface BeanDefinition {

    Class<?> getClazz();

    Class<?>[] getParameters();

    Object register(Object[] parameters);
}
