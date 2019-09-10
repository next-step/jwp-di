package core.di.scanner;

import com.google.common.collect.ImmutableSet;
import core.annotation.Configuration;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ScannableAnnotaionTypesTest {

    private ScannableAnnotaionTypes scannableAnnotaionTypes;

    @BeforeEach
    void setup() {
        scannableAnnotaionTypes = new ScannableAnnotaionTypes();
    }

    @Test
    void getAllTypes() throws Exception {
        Set<Class<? extends Annotation>> allTypes = scannableAnnotaionTypes.getAllTypes();

        Set<Class<? extends Annotation>> expected = ImmutableSet.of(Configuration.class, Controller.class, Service.class, Repository.class);

        assertThat(allTypes).isEqualTo(expected);
    }

    @ParameterizedTest(name = "[{index}] {0} : {1}")
    @MethodSource("arguments")
    void isScannable(Class<?> clazz, boolean expected) throws Exception {

        boolean scannable = ScannableAnnotaionTypes.isScannable(clazz);

        assertThat(scannable).isEqualTo(expected);
    }

    private static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of(ConfigurationClass.class, true),
                Arguments.of(ControllerClass.class, true),
                Arguments.of(ServiceClass.class, true),
                Arguments.of(RepositoryClass.class, true),
                Arguments.of(NoneClass.class, false)
        );
    }


    @Configuration
    public static class ConfigurationClass {}

    @Controller
    public static class ControllerClass {}

    @Service
    public static class ServiceClass {}

    @Repository
    public static class RepositoryClass {}

    public static class NoneClass {}
}