package core.di.factory.illegal.configuration;

import core.annotation.Bean;
import core.annotation.Configuration;

@Configuration
public class IllegalConfig {

    @Bean
    public ExampleController exampleController() {
        return new ExampleController();
    }

}
