package core.mvc.tobe;

import core.annotation.Bean;
import core.annotation.ComponentScan;
import core.annotation.Configuration;
import core.di.AnnotationConfigApplicationContext;
import core.di.ApplicationContext;
import next.dao.UserDao;
import next.model.User;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import support.test.DBInitializer;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

class AnnotationHandlerMappingTest {
    private AnnotationHandlerMapping handlerMapping;
    private UserDao userDao;

    @BeforeEach
    public void setup() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(TestConfiguration.class);
        handlerMapping = new AnnotationHandlerMapping(applicationContext);
        handlerMapping.initialize();
        userDao = applicationContext.getBean(UserDao.class);

        DBInitializer.initialize();
    }

    @Test
    void create_find() throws Exception {
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

    @Configuration
    @ComponentScan({"core.jdbc", "core.mvc.tobe", "next.dao"})
    public static class TestConfiguration {
        @Bean
        public DataSource dataSource() {
            BasicDataSource ds = new BasicDataSource();
            ds.setDriverClassName("org.h2.Driver");
            ds.setUrl("jdbc:h2:mem://localhost/~/jwp-di;MODE=MySQL;DB_CLOSE_DELAY=-1");
            ds.setUsername("sa");
            ds.setPassword("");
            return ds;
        }
    }
}
