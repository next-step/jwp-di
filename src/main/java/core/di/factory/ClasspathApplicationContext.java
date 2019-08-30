package core.di.factory;

public class ClasspathApplicationContext extends ApplicationContext {

    public ClasspathApplicationContext(String... basePackages) {
        this.beanFactory = new SimpleBeanFactory();
        new ClasspathBeanScanner(beanFactory).scan(basePackages);
        this.beanFactory.initialize();
    }

}
