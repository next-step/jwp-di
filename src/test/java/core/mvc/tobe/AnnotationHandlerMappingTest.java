package core.mvc.tobe;

import static org.assertj.core.api.Assertions.assertThat;

import core.di.factory.ApplicationContext;
import javax.sql.DataSource;
import next.dao.UserDao;
import next.model.User;
import next.support.config.MyWebAppConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import support.test.DBInitializer;

public class AnnotationHandlerMappingTest {

    private AnnotationHandlerMapping handlerMapping;
    private UserDao userDao;

    @BeforeEach
    public void setup() {
        ApplicationContext ac = new ApplicationContext(MyWebAppConfiguration.class);
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

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/users");
        request.setParameter("userId", user.getUserId());
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerExecution execution = (HandlerExecution) handlerMapping.getHandler(request);
        execution.handle(request, response);
    }

    private void createUser(User user) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/users");
        request.setParameter("userId", user.getUserId());
        request.setParameter("password", user.getPassword());
        request.setParameter("name", user.getName());
        request.setParameter("email", user.getEmail());
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerExecution execution = (HandlerExecution) handlerMapping.getHandler(request);
        execution.handle(request, response);
    }
}
