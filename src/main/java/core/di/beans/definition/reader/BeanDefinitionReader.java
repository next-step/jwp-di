package core.di.beans.definition.reader;

public interface BeanDefinitionReader {
    void read(Class<?>... types);
}