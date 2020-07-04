package core.di.context;

public class AnnotationConfigApplicationContext implements ApplicationContext {

    public AnnotationConfigApplicationContext(Class<?>... configClasses) {

    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return null;
    }
}
