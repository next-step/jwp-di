package core.di.beans.definition;

public interface BeanDefinitionReader {
    void read(Class<?>... types);
}