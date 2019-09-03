package core.context;

import core.di.context.AnnotationConfigApplicationContext;
import core.di.factory.example.IntegrationConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AnnotationConfigApplicationContextTest {

    @Test
    void scan() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(IntegrationConfig.class);
        assertThat(context.getBasePackages()).isEqualTo(new String[] {"core.di.factory.example"});
    }
}