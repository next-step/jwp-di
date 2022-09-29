package core.di.scanner;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.BeanFactory;
import core.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Set;

public class ClasspathBeanScanner implements Scanner {

    private static final Logger logger = LoggerFactory.getLogger(ClasspathBeanScanner.class);

    @Override
    public void scan(BeanFactory beanFactory, String... basePackage) {
        Set<Class<?>> preInstantiatedBeans = ReflectionUtils.getPreInstantiatedBeansWithAnnotations(Arrays.asList(Controller.class, Service.class, Repository.class), basePackage);
        logger.debug("ClasspathBeanScanner scan completed ..... list below");
        preInstantiatedBeans.forEach(preInstantiatedBean -> logger.debug(preInstantiatedBean.getName()));
        logger.debug("----------------------------------------------------");
        preInstantiatedBeans.forEach(beanFactory::addPreInstantiatedBeans);
    }

}
