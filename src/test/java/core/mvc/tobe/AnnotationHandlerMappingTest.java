package core.mvc.tobe;

import core.mvc.ModelAndView;
import next.config.MyConfiguration;
import next.dao.UserDao;
import next.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import support.test.DBInitializer;

import javax.servlet.http.HttpSession;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AnnotationHandlerMappingTest {
    private AnnotationHandlerMapping handlerMapping;
    private UserDao userDao;

    @BeforeEach
    public void setup() {
        handlerMapping = new AnnotationHandlerMapping(MyConfiguration.class);
        handlerMapping.initialize();

        DBInitializer.initialize();
        userDao = handlerMapping.applicationContext().getBean(UserDao.class);
    }

    @Test
    public void create_find() throws Exception {
        User user = new User("pobi", "password", "포비", "pobi@nextstep.camp");
        createUser(user);
        assertThat(userDao.findByUserId(user.getUserId())).isEqualTo(user);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/users/profile");
        request.setParameter("userId", user.getUserId());

        HandlerExecution execution = (HandlerExecution)handlerMapping.getHandler(request);
        ModelAndView mav = execution.handle(request, response);
        Map<String, Object> model = mav.getModel();

        assertThat(model.get("user")).isEqualTo(user);
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
