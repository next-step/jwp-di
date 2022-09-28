package core.mvc.tobe;

import core.di.factory.BeanFactory;
import core.di.factory.example.JdbcQuestionRepository;
import core.di.factory.example.JdbcUserRepository;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;
import next.dao.UserDao;
import next.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import support.test.DBInitializer;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AnnotationHandlerMappingTest {
    private AnnotationHandlerMapping handlerMapping;
    private UserDao userDao;

    @BeforeEach
    public void setup() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        BeanFactory.getInstance().initialize("core.mvc.tobe");
        handlerMapping = new AnnotationHandlerMapping();
        handlerMapping.initialize();

        DBInitializer.initialize();
        userDao = UserDao.getInstance();
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

        assertThat(request.getAttribute("user")).isEqualTo(user);
    }

    @Test
    void getHandlersTest() {
        QnaController qnaController = new QnaController(new MyQnaService(new JdbcUserRepository(), new JdbcQuestionRepository()));
        Map<Class<?>, Object> beans = new HashMap<>() {{
            put(QnaController.class, qnaController);
        }};
        Map<HandlerKey, HandlerExecution> handlers = handlerMapping.getHandlers(beans);

        Assertions.assertThat(handlers.size()).isEqualTo(1);
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
