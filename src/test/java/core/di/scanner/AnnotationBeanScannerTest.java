package core.di.scanner;

import core.di.factory.example.*;
import core.di.bean.AnnotationBeanDefinition;
import core.di.bean.BeanDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class AnnotationBeanScannerTest {

    private static final Logger log = LoggerFactory.getLogger(AnnotationBeanDefinition.class);

    private static final String DI_DEFAULT_PACKAGE = "core.di.factory.example";
    private BeanScanner beanScanner;

    @BeforeEach
    void setUp() {
        beanScanner = new AnnotationBeanScanner(DI_DEFAULT_PACKAGE);
    }

    @Test
    void scan() {
        Set<BeanDefinition> beans = beanScanner.scan();

        Set<? extends Class<?>> distinctBean = beans.stream()
                .map(BeanDefinition::getClazz).collect(Collectors.toSet());

        log.debug("Configuration class scan : {}", beans);

        assertThat(beans).hasSize(3);
        assertThat(distinctBean.contains(DataSource.class)).isTrue();
        assertThat(distinctBean.contains(MyJdbcTemplate.class)).isTrue();
    }

}