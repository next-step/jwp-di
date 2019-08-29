package core.di.factory;

import core.di.factory.config.BeanDefinition;
import core.jdbc.JdbcTemplate;
import next.MyConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationBeanDefinitionReaderTest {


    @DisplayName("ConfigurationBeanDefinitionReader test")
    @Test
    public void readerTest(){
        BeanFactory beanFactory = new BeanFactory();
        ConfigurationBeanDefinitionReader configurationBeanDefinitionReader = new ConfigurationBeanDefinitionReader(beanFactory);
        Set<BeanDefinition> beanDefinitions = configurationBeanDefinitionReader.getBeanDefinitions(MyConfiguration.class);
        assertThat(beanDefinitions).isNotNull();
        assertThat(beanDefinitions).isNotEmpty();
        List<Class<?>> beanTypes = beanDefinitions.stream()
                .map(BeanDefinition::getBeanType)
                .collect(Collectors.toList());
        assertThat(beanTypes).contains(DataSource.class);
        assertThat(beanTypes).contains(JdbcTemplate.class);
    }
}
