package core.di.factory;

import core.di.ConfigurationScanner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created By kjs4395 on 7/18/20
 */
public class ConfigurationScannerTest {

    @Test
    public void configurationScannerTest() {
        ConfigurationScanner configurationScanner = new ConfigurationScanner();
        configurationScanner.initialize();

        assertNotNull(configurationScanner.beans());
    }
}
