package core.di.factory.container;

import java.util.Collection;

public interface ApplicationContext {

    void initialize();

    Collection<Object> getControllers();
}
