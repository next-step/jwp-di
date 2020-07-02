package core.di.factory;

/**
 * @author KingCjy
 */
public interface BeanDefinition {
    Class<?> getType();
    String getName();
}
