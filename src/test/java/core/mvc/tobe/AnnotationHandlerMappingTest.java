package core.mvc.tobe;

import core.di.context.AnnotationConfigApplicationContext;
import core.di.context.ApplicationContext;
import core.di.factory.example.IntegrationConfig;
import core.mvc.ModelAndView;
import javax.sql.DataSource;
import next.config.NextConfiguration;
import next.controller.ApiUserController;
import next.controller.UserController;
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
        ApplicationContext ac = new AnnotationConfigApplicationContext(IntegrationConfig.class);

        DBInitializer.initialize(ac.getBean(DataSource.class));

        handlerMapping = new AnnotationHandlerMapping(ac);
        handlerMapping.initialize();

        userDao = ac.getBean(UserDao.class);
    }

    @Test
    public void create_find() throws Exception {
        User user = new User("pobi", "password", "포비", "pobi@nextstep.camp");
        createUser(user);
        assertThat(userDao.findByUserId(user.getUserId())).isEqualTo(user);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/users/profile");
        request.setParameter("userId", user.getUserId());
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerExecution execution = (HandlerExecution)handlerMapping.getHandler(request);
        ModelAndView modelAndView = execution.handle(request, response);

        assertThat(modelAndView.getObject("user")).isEqualTo(user);
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
