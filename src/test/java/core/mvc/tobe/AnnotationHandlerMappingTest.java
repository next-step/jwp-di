package core.mvc.tobe;

import core.di.factory.AnnotationBeanFactory;
import core.jdbc.JdbcConfig;
import core.jdbc.JdbcTemplate;
import next.dao.UserDao;
import next.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import support.test.DBInitializer;

import static org.assertj.core.api.Assertions.assertThat;

public class AnnotationHandlerMappingTest {
    private AnnotationHandlerMapping handlerMapping;
    private UserDao userDao;

    @BeforeEach
    public void setup() {
        ComponentBeanScanner componentBeanScanner = new ComponentBeanScanner(new AnnotationBeanFactory());
        componentBeanScanner.scan("core.mvc.tobe");

        handlerMapping = new AnnotationHandlerMapping(componentBeanScanner);
        handlerMapping.initialize();

        DBInitializer.initialize();
        JdbcConfig jdbcConfig = new JdbcConfig();
        userDao = new UserDao(new JdbcTemplate(jdbcConfig.dataSource()));
    }

    @Test
    public void create_find() throws Exception {
        User user = new User("pobi", "password", "포비", "pobi@nextstep.camp");
        createUser(user);
        assertThat(userDao.findByUserId(user.getUserId())).isEqualTo(user);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/users");
        request.setParameter("userId", user.getUserId());
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
