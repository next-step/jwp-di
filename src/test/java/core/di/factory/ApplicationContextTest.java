package core.di.factory;

import core.db.MyConfiguration;
import core.di.ApplicationContext;
import core.mvc.tobe.AnnotationHandlerMapping;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author : yusik
 * @date : 03/09/2019
 */
public class ApplicationContextTest {

    @DisplayName("Application context 초기화 테스트")
    @Test
    void test() {
        ApplicationContext ac = new ApplicationContext(MyConfiguration.class);
        AnnotationHandlerMapping ahm = new AnnotationHandlerMapping(ac);
        ahm.initialize();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/users");

        assertThat(ahm.getHandler(request)).isNotNull();
    }
}
