package core.di.factory;

public interface ConfigurableListableBeanFactory extends ListableBeanFactory {

    void preInstantiateSingletons();
}
