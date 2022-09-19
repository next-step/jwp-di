package core.di.factory;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class ClassPathBeanScanner {

    private static final Logger logger = LoggerFactory.getLogger(ClassPathBeanScanner.class);

    private BeanDefinitions beanDefinitions;


    public ClassPathBeanScanner(BeanDefinitions beanDefinitions) {
        this.beanDefinitions = beanDefinitions;
    }

    public void doScan(Object... basePackage) {
        BeanScanner scanner = new BeanScanner();
        beanDefinitions.addAllPreInstantiateBeans(scanner.scan(basePackage));
    }
}
