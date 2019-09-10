package core.mvc.tobe;

import core.db.DataBase;
import core.di.factory.BeanFactory;
import core.di.scanner.ClasspathBeanScanner;
import next.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class AnnotationHandlerMappingTest {
    private AnnotationHandlerMapping handlerMapping;

    @BeforeEach
    public void setup() {
        BeanFactory beanFactory = new BeanFactory();
        ClasspathBeanScanner beanScanner = new ClasspathBeanScanner(beanFactory);
        beanScanner.doScan("core.mvc.tobe");

        handlerMapping = new AnnotationHandlerMapping(beanScanner, beanFactory);
        handlerMapping.initialize();
    }

    @Test
    public void create_find() throws Exception {
        User user = new User("pobi", "password", "포비", "pobi@nextstep.camp");
        createUser(user);
        assertThat(DataBase.findUserById(user.getUserId())).isEqualTo(user);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/my_users");
        request.setParameter("userId", user.getUserId());
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerExecution execution = (HandlerExecution)handlerMapping.getHandler(request);
        execution.handle(request, response);

        assertThat(request.getAttribute("user")).isEqualTo(user);
    }

    private void createUser(User user) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/my_users");
        request.setParameter("userId", user.getUserId());
        request.setParameter("password", user.getPassword());
        request.setParameter("name", user.getName());
        request.setParameter("email", user.getEmail());
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerExecution execution = (HandlerExecution)handlerMapping.getHandler(request);
        execution.handle(request, response);
    }
}
