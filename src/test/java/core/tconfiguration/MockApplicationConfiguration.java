package core.tconfiguration;

import core.annotation.ComponentScan;
import core.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"core, next"})
public class MockApplicationConfiguration {

    public MockApplicationConfiguration() {
    }

    public class MockParameterClass {

    }

}
