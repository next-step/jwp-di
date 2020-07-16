package core.di.factory;

import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyQnaService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PreInstantiateBeansTest {

    @Test
    public void repositoryCreateBeanObject() {
        PreInstantiateBeans preInstantiateBeans = new PreInstantiateBeans();

        Object object = preInstantiateBeans.createBeanObject(JdbcUserRepository.class);
        assertThat(object).isNotEqualTo(null);
    }

    @Test
    public void serviceCreateBeanObject() {
        PreInstantiateBeans preInstantiateBeans = new PreInstantiateBeans();

        Object object = preInstantiateBeans.createBeanObject(MyQnaService.class);
        assertThat(object).isNotEqualTo(null);
    }

}
