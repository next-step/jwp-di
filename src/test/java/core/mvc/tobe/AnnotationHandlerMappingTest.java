package core.mvc.tobe;

import static org.assertj.core.api.Assertions.assertThat;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import core.di.scanner.BeanScanner;
import next.model.User;
import support.test.DBInitializer;

public class AnnotationHandlerMappingTest {
    private AnnotationHandlerMapping handlerMapping;

    @BeforeEach
    public void setup() {
        BeanScanner beanScanner = new BeanScanner("core.di");
        beanScanner.scan();
        beanScanner.beanInitialize();

        handlerMapping = new AnnotationHandlerMapping(beanScanner);
        handlerMapping.initialize();

        DBInitializer.initialize(beanScanner.getBeanFactory().getBean(DataSource.class));
    }

    @Test
    public void create_find() throws Exception {
        User user = new User("pobi", "password", "포비", "pobi@nextstep.camp");
        createUser(user);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/users");
        request.setParameter("userId", user.getUserId());
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerExecution execution = (HandlerExecution)handlerMapping.getHandler(request);
        execution.handle(request, response);

        assertThat(request.getAttribute("user")).isEqualTo(user);
    }

    private void createUser(User user) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/users");
        request.setParameter("userId", user.getUserId());
        request.setParameter("password", user.getPassword());
        request.setParameter("name", user.getName());
        request.setParameter("email", user.getEmail());
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerExecution execution = (HandlerExecution)handlerMapping.getHandler(request);
        execution.handle(request, response);
    }
}
