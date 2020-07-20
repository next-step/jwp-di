package core.di.factory;

public class ClasspathBeanScanner {

    private final BeanFactory beanFactory;

    public ClasspathBeanScanner(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void doScan(Object... basePackage) {
        BeanScanner beanScanner = new BeanScanner();
        beanFactory.addAllPreInstantiateBeans(beanScanner.scan(basePackage));
    }
}
