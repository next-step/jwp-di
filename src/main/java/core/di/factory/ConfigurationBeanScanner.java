package core.di.factory;

import core.annotation.Bean;
import core.config.WebMvcConfiguration;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigurationBeanScanner implements BeanScanner {

    @Override
    public Set<BeanRegister> scan(Class<? extends WebMvcConfiguration> clazz) {
        Set<BeanRegister> beanRegisters = Arrays.stream(clazz.getMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .map(MethodBeanRegister::new)
                .collect(Collectors.toSet());

        beanRegisters.add(new ClassBeanRegister(clazz));

        return beanRegisters;
    }
}
