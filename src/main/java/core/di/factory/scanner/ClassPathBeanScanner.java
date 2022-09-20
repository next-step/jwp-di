package core.di.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassPathBeanScanner {

    private static final Logger logger = LoggerFactory.getLogger(ClassPathBeanScanner.class);

    private BeanDefinitionRegistry beanDefinitions;


    public ClassPathBeanScanner(BeanDefinitionRegistry beanDefinitions) {
        this.beanDefinitions = beanDefinitions;
    }

    public void doScan(Object... basePackage) {
        BeanScanner scanner = new BeanScanner();
        beanDefinitions.addAllPreInstantiateBeans(scanner.scan(basePackage));
    }
}
