package next;

import core.config.WebMvcConfiguration;
import core.di.factory.BeanFactory;
import core.di.factory.BeanRegister;
import core.di.factory.BeanScanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationContext {
    private final Class<? extends WebMvcConfiguration> configurationClazz;

    private BeanFactory beanFactory;
    private final List<BeanScanner> beanScanners = new ArrayList<>();

    public ApplicationContext(Class<? extends WebMvcConfiguration> configurationClazz) {
        this.configurationClazz = configurationClazz;
    }

    public void addScanner(BeanScanner scanner) {
        beanScanners.add(scanner);
    }

    public void initialize() {
        Set<BeanRegister> beanRegisters = beanScanners.stream()
                .map(scanner -> scanner.scan(configurationClazz))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        beanFactory = new BeanFactory();
        beanFactory.register(beanRegisters.toArray(BeanRegister[]::new));
        beanFactory.initialize();
    }

    public <T> T getBean(Class<T> requiredType) {
        return beanFactory.getBean(requiredType);
    }

    public Set<Class<?>> getControllers() {
        return beanFactory.getControllers();
    }
}
