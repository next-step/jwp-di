package core.mvc.tobe.support;

import core.mvc.tobe.MethodParameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ArgumentResolversTest {

    private List<ArgumentResolver> resolverList;

    @BeforeEach
    void setUp() {
        resolverList = List.of(new HttpRequestArgumentResolver(),
                new HttpResponseArgumentResolver(),
                new RequestParamArgumentResolver(),
                new PathVariableArgumentResolver(),
                new ModelArgumentResolver());
    }

    @DisplayName("addResolver 메서드는 유효한 리졸버를 전달하면 리졸버를 추가하고 일급컬렉션 자신을 반환한다.")
    @Test
    void addResolver() {
        ArgumentResolvers resolvers = new ArgumentResolvers();
        List<ArgumentResolver> store = (List) ReflectionTestUtils.getField(resolvers, "store");

        for (ArgumentResolver resolver : resolverList) {
            ArgumentResolvers resultResolvers = resolvers.addResolver(resolver);
            assertThat(resultResolvers).isEqualTo(resolvers);
        }

        assertThat(store).hasSize(resolverList.size());
    }

    @DisplayName("findByMethodParameter 메소드는")
    @Nested
    class Describe_findByMethodParameter {
        @DisplayName("유효한 MethodParameter를 전달하면 적절한 리졸버를 반환한다.")
        @Test
        void findByMethodParameterWithSupportedMethodParameter() {
            MethodParameter methodParameter = new MethodParameter(null, HttpServletRequest.class, new Annotation[]{}, "");

            ArgumentResolvers resolvers = new ArgumentResolvers();
            resolvers.addResolver(new HttpRequestArgumentResolver());
            resolvers.addResolver(new HttpResponseArgumentResolver());

            Optional<ArgumentResolver> result = resolvers.findByMethodParameter(methodParameter);

            assertThat(result).isPresent();
            assertThat(result.get()).isInstanceOf(HttpRequestArgumentResolver.class);
        }

        @DisplayName("유효하지 않은 MethodParameter를 전달하면 비어있는 옵셔널 객체를 반환한다..")
        @Test
        void findByMethodParameterWithNotSupportedMethodParameter() {
            MethodParameter methodParameter = new MethodParameter(null, HttpServletRequest.class, new Annotation[]{}, "");

            ArgumentResolvers resolvers = new ArgumentResolvers();
            resolvers.addResolver(new HttpResponseArgumentResolver());

            Optional<ArgumentResolver> result = resolvers.findByMethodParameter(methodParameter);

            assertThat(result).isEmpty();
        }
    }

}
