package core.mvc.tobe;

import core.annotation.web.RequestMethod;
import core.di.factory.BeanDefinition;
import core.di.factory.BeanFactory;
import core.di.factory.BeanScanner;
import core.di.factory.DefaultBeanFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author KingCjy
 */
public class ControllersTest {

    private Controllers controllers;

    @BeforeEach
    public void setUp() {
        DefaultBeanFactory beanFactory = new DefaultBeanFactory();
        BeanScanner beanScanner = new BeanScanner(beanFactory);
        beanScanner.scan("core.di.factory.example");
        beanFactory.initialize();

        controllers = new Controllers(beanFactory);
    }

    @Test
    public void initTest() {
        Map<HandlerKey, HandlerExecution> handlerExecutions =  controllers.getHandlerExecutions();

        System.out.println(handlerExecutions);
        assertThat(handlerExecutions).containsKey(new HandlerKey("/questions", RequestMethod.GET));
    }
}
