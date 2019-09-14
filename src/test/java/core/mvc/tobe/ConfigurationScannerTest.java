package core.mvc.tobe;

import core.di.factory.example.ExampleConfig;
import core.di.factory.example.IntegrationConfig;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationScannerTest {

    @Test
    void ConfigurationScanner에서_패키지경로로_Bean설정() {
        ConfigurationScanner scanner = new ConfigurationScanner("core.di.factory.example");
        final Set<Class<?>> types = scanner.getTypes();

        assertThat(types).contains(IntegrationConfig.class);
        assertThat(types).contains(ExampleConfig.class);
    }

    @Test
    void ConfigurationScanner으로_Bean설정() {
        ConfigurationScanner scanner = new ConfigurationScanner();
        final Set<Class<?>> types = scanner.getTypes();

        assertThat(types).contains(IntegrationConfig.class);
        assertThat(types).contains(ExampleConfig.class);
    }
}