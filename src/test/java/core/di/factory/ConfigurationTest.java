package core.di.factory;

import core.annotation.Configuration;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import javax.sql.DataSource;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created By kjs4395 on 7/17/20
 */
public class ConfigurationTest {

    @Test
    public void ConfigurationFindTest() {
        Reflections reflections = new Reflections("");

        ConfigurationFactory configurationFactory = new ConfigurationFactory(reflections.getTypesAnnotatedWith(Configuration.class));

        configurationFactory.initialize();

        assertNotNull(configurationFactory.getBean(DataSource.class));
    }
}
