package core.di.tobe.scanner;

import core.di.factory.example.*;
import core.di.tobe.bean.BeanDefinition;
import core.di.tobe.BeanFactory;
import core.di.tobe.bean.ConfigurationBeanDefinition;
import core.di.tobe.DefaultBeanFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationBeanScannerTest {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationBeanDefinition.class);

    private static final String DI_DEFAULT_PACKAGE = "core.di.factory.example";
    private BeanFactory beanFactory = DefaultBeanFactory.getInstance();
    private BeanScanner beanScanner;

    @BeforeEach
    void setUp() {
        beanScanner = new ConfigurationBeanScanner(beanFactory, DI_DEFAULT_PACKAGE);
    }

    @Test
    void enroll() {
        Set<BeanDefinition> beans = beanScanner.enroll();

        Set<? extends Class<?>> distinctBean = beans.stream()
                .map(BeanDefinition::getClazz).collect(Collectors.toSet());

        log.debug("Configuration class scan : {}", beans);

        assertThat(beans).hasSize(3);
        assertThat(distinctBean.contains(MyJdbcTemplate.class)).isTrue();
        assertThat(distinctBean.contains(MyJdbcTemplate.class)).isTrue();
    }

}