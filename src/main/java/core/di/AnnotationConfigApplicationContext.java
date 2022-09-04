package core.di;

import java.util.Arrays;
import java.util.stream.Collectors;

import core.annotation.ComponentScan;

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
        Object[] basePackages = Arrays.stream(componentClasses)
            .filter(componentClass -> componentClass.isAnnotationPresent(ComponentScan.class))
            .map(componentClass -> componentClass.getAnnotation(ComponentScan.class))
            .map(ComponentScan::value)
            .toArray();

        for (Object basePackage : basePackages) {
            System.out.println("basePackage = " + basePackage);
        }
    }

    private void scan(Object... basePackages) {
        this.scanner.scan(basePackages);
    }
}
