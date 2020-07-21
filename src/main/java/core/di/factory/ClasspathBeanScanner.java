package core.di.factory;

public class ClasspathBeanScanner {

    private final BeanDefinitions beanDefinition;

    public ClasspathBeanScanner(BeanDefinitions beanDefinition) {
        this.beanDefinition = beanDefinition;
    }

    public void doScan(Object... basePackage) {
        BeanScanner beanScanner = new BeanScanner();
        beanDefinition.addAllPreInstantiateBeans(beanScanner.scan(basePackage));
    }
}
