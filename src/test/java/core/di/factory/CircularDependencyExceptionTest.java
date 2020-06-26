package core.di.factory;

import core.di.exception.CircularDependencyException;
import core.di.factory.circular.OneComponent;
import core.di.factory.circular.ThreeComponent;
import core.di.factory.circular.TwoComponent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedHashSet;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("circular dependency")
public class CircularDependencyExceptionTest {
    private static final Logger logger = LoggerFactory.getLogger(CircularDependencyExceptionTest.class);

    @Test
    @DisplayName("의존성이 물리고 물려서 생성이 안되는 경우 테스트")
    void circularDependency() {
        BeanFactory beanFactory = new BeanFactory(
                new LinkedHashSet<>(
                        Arrays.asList(OneComponent.class, TwoComponent.class, ThreeComponent.class)
                )
        );

        assertThatExceptionOfType(CircularDependencyException.class)
                .isThrownBy(beanFactory::initialize)
                .withMessageStartingWith("There is circular dependency");

        try {
            beanFactory.initialize();
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }
}
