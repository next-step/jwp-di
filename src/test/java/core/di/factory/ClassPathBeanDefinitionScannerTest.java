package core.di.factory;

import core.di.factory.config.BeanDefinition;
import core.mvc.tobe.MyController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassPathBeanDefinitionScannerTest {


    @DisplayName("scan test")
    @Test
    public void scanTest(){
        BeanFactory beanFactory = new BeanFactory();
        ClassPathBeanDefinitionScanner classPathBeanDefinitionScanner = new ClassPathBeanDefinitionScanner(beanFactory);
        Set<BeanDefinition> beanDefinitions = classPathBeanDefinitionScanner.getBeanDefinitions("core.mvc.tobe");
        assertThat(beanDefinitions).isNotNull();
        assertThat(beanDefinitions).isNotEmpty();
        List<Class<?>> beanTypes = beanDefinitions.stream()
                .map(BeanDefinition::getBeanType)
                .collect(Collectors.toList());
        assertThat(beanTypes).contains(MyController.class);
    }
}
