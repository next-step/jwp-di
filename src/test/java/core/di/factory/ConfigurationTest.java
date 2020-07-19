package core.di.factory;

import core.annotation.Configuration;
import core.di.factory.example.ExampleConfig;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.MemberUsageScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ConfigurationTest {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationTest.class);

    @Test
    public void scanConfiguration() {
        Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(""))
                .addScanners(new MemberUsageScanner(), new MethodAnnotationsScanner(), new MethodParameterScanner()));
        Set<Class<?>> configurationClasses = reflections.getTypesAnnotatedWith(Configuration.class);

        configurationClasses.contains(ExampleConfig.class);
    }
}
