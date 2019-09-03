package core.context;

import core.di.factory.BeanFactory;

public interface ApplicationContext extends BeanFactory {
    String getApplicationName();
}
