package core.di.factory;

import core.config.WebMvcConfiguration;

import java.util.Set;

public interface BeanScanner {

    Set<BeanRegister> scan(Class<? extends WebMvcConfiguration> clazz);
}
