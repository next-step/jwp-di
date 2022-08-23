package core.mvc.tobe.support;

import core.mvc.tobe.MethodParameter;
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


    @DisplayName("addResolver 메서드는 유효한 리졸버를 전달하면 리졸버를 추가하고 일급컬렉션 자신을 반환한다.")
    @Test
    void addResolver() {
        List<ArgumentResolver> resolverList = List.of(new HttpRequestArgumentResolver(),
                new HttpResponseArgumentResolver(),
                new RequestParamArgumentResolver(),
                new PathVariableArgumentResolver(),
                new ModelArgumentResolver());

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
    class DescribeFindByMethodParameter {

        @DisplayName("HttpServletRequest타입의 MethodParameter를 전달하면 ")
        @Nested
        class ContextWithSupportedMethodParameter {

            @DisplayName("HttpRequestArgumentResolver 리졸버를 반환한다.")
            @Test
            void itReturnsValidResolver() {
                MethodParameter methodParameter = new MethodParameter(null, HttpServletRequest.class, new Annotation[]{}, "");

                ArgumentResolvers resolvers = new ArgumentResolvers();
                resolvers.addResolver(new HttpRequestArgumentResolver());
                resolvers.addResolver(new HttpResponseArgumentResolver());

                Optional<ArgumentResolver> result = resolvers.findByMethodParameter(methodParameter);

                assertThat(result).isPresent();
                assertThat(result.get()).isInstanceOf(HttpRequestArgumentResolver.class);
            }
        }

        @DisplayName("지원되지 않는 MethodParameter를 전달하면")
        @Nested
        class ContextWithNotSupportedMethodParameter {

            @DisplayName("비어있는 옵셔널 객체를 반환한다.")
            @Test
            void itReturnsEmptyOptional() {
                MethodParameter methodParameter = new MethodParameter(null, HttpServletRequest.class, new Annotation[]{}, "");

                ArgumentResolvers resolvers = new ArgumentResolvers();
                resolvers.addResolver(new HttpResponseArgumentResolver());

                Optional<ArgumentResolver> result = resolvers.findByMethodParameter(methodParameter);

                assertThat(result).isEmpty();
            }
        }
    }

}
