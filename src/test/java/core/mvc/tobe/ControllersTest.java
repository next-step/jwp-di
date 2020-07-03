package core.mvc.tobe;

import core.annotation.web.Controller;
import core.annotation.web.RequestMethod;
import core.di.factory.ClassBeanScanner;
import core.di.factory.DefaultBeanFactory;
import core.mvc.tobe.support.ArgumentResolverComposite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        ClassBeanScanner classBeanScanner = new ClassBeanScanner(beanFactory);
        classBeanScanner.scan("core.di.factory.example");
        beanFactory.initialize();

        controllers = new Controllers(beanFactory.getAnnotatedBeans(Controller.class), new ArgumentResolverComposite());
    }

    @Test
    public void initTest() {
        Map<HandlerKey, HandlerExecution> handlerExecutions =  controllers.getHandlerExecutions();

        System.out.println(handlerExecutions);
        assertThat(handlerExecutions).containsKey(new HandlerKey("/questions", RequestMethod.GET));
    }
}
