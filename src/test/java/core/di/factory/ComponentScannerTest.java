package core.di.factory;

import core.annotation.Configuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("컴포넌트 스캐너")
class ComponentScannerTest {
    private static final Logger logger = LoggerFactory.getLogger(ComponentScannerTest.class);
    private static final String PACKAGE = "core.di.factory.example";

    @Test
    @DisplayName("컴포넌트 스캐너는 package 를 기반으로 클래스를 로드한다")
    void initialize() {
        Set<Class<?>> classes = ComponentScanner.scan(PACKAGE);

        assertThat(classes).isNotNull();
        assertThat(classes).isNotEmpty();
        logger.debug("SIZE : {}", classes.size());
    }

    @Test
    @DisplayName("컴포넌트 스캐너는 원하는 타입의 어노테이션을 받아서 클래스를 로드할 수 있다")
    void initWithAnnotation() {
        Set<Class<?>> classes = ComponentScanner.scan(
                Collections.singletonList(Configuration.class),
                "core.di.factory.example"
        );

        assertThat(classes).isNotNull();
        assertThat(classes).isNotEmpty();

        assertThat(
                classes.stream()
                        .allMatch(clazz -> clazz.getAnnotation(Configuration.class) != null)
        ).isTrue();
    }
}
