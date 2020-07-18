package core.di;

import core.annotation.Configuration;
import core.di.factory.ConfigurationFactory;
import org.reflections.Reflections;

import java.util.Map;
import java.util.Set;

/**
 * Created By kjs4395 on 7/18/20
 */
public class ConfigurationScanner {

    private ConfigurationFactory configurationFactory;

    public void initialize() {
        Reflections reflections = new Reflections("");
        Set<Class<?>> configurations = reflections.getTypesAnnotatedWith(Configuration.class);

        configurationFactory = new ConfigurationFactory(configurations);
        configurationFactory.initialize();
    }

    public Map<Class<?>,Object> beans() {
        return configurationFactory.getAllBeans();
    }

    public Object[] basePackages() {
        return configurationFactory.getComponentScan();
    }

}
