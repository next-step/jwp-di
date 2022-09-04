package core.di;

public class AnnotationConfigApplicationContext extends GenericApplicationContext {

    private final ClassPathBeanDefinitionScanner scanner;
    private final AnnotatedBeanDefinitionReader reader;

    public AnnotationConfigApplicationContext() {
        scanner = new ClassPathBeanDefinitionScanner(this);
        reader = new AnnotatedBeanDefinitionReader(this);
    }

    public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
        this();
        register(componentClasses);
        refresh();
    }

    public AnnotationConfigApplicationContext(Object... basePackages) {
        this();
        scan(basePackages);
        refresh();
    }

    private void register(Class<?>[] componentClasses) {
        this.reader.register(componentClasses);
    }

    private void scan(Object... basePackages) {
        this.scanner.scan(basePackages);
    }
}
