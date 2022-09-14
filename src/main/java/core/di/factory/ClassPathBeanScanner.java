package core.di.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassPathBeanScanner {

    private static final Logger logger = LoggerFactory.getLogger(ClassPathBeanScanner.class);

    private final BeanFactory beanFactory;

    public ClassPathBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void doScan(Object... basePackage) {
        BeanScanner scanner = new BeanScanner();
        beanFactory.addAllPreInstantiateBeans(scanner.scan(basePackage));
    }
}
