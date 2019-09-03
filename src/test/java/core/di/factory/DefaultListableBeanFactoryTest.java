package core.di.factory;

import core.di.factory.config.AnnontatedBeanDefinition;
import core.di.factory.config.DefaultBeanDefinition;
import core.di.factory.example.IntegrationConfig;
import core.di.factory.example.MyJdbcTemplate;
import core.di.factory.support.DefaultListableBeanFactory;
import next.controller.ApiQnaController;
import next.dao.AnswerDao;
import next.dao.QuestionDao;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DefaultListableBeanFactoryTest {

    @Test
    void getClassPathBean() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerBeanDefinition(new DefaultBeanDefinition(ApiQnaController.class));
        beanFactory.registerBeanDefinition(new DefaultBeanDefinition(QuestionDao.class));
        beanFactory.registerBeanDefinition(new DefaultBeanDefinition(AnswerDao.class));
        beanFactory.instantiateBeans();

        ApiQnaController bean = beanFactory.getBean(ApiQnaController.class);
        assertNotNull(bean);
    }

    @Test
    void getAnnotatedBean() throws NoSuchMethodException {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        Class<?> targetClass = IntegrationConfig.class;
        Method dataSource = targetClass.getMethod("dataSource");
        Method jdbcTemplate = targetClass.getMethod("jdbcTemplate", DataSource.class);
        beanFactory.registerBeanDefinition(new AnnontatedBeanDefinition(dataSource.getReturnType(),dataSource));
        beanFactory.registerBeanDefinition(new AnnontatedBeanDefinition(jdbcTemplate.getReturnType(),jdbcTemplate));
        beanFactory.instantiateBeans();

        DataSource dataSourceBean = beanFactory.getBean(DataSource.class);
        MyJdbcTemplate jdbcTemplateBean = beanFactory.getBean(MyJdbcTemplate.class);

        assertNotNull(dataSourceBean);
        assertNotNull(jdbcTemplateBean);
    }
}

