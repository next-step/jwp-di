package core.di.factory;

import core.di.factory.example.JdbcUserRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PreInstantiateBeansTest {

    @Test
    public void repositoryDi() {
        PreInstantiateBeans preInstantiateBeans = new PreInstantiateBeans();

        Object object = preInstantiateBeans.createBeanObject(JdbcUserRepository.class);
        assertThat(object).isNotEqualTo(null);
    }

}
