package core.context.annotation;

import core.context.ApplicationContext;
import core.di.factory.AnnotatedBeanDefinitionReader;
import core.di.factory.BeanFactoryUtils;
import core.di.factory.ClassPathBeanDefinitionScanner;
import core.di.factory.support.DefaultListableBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Map;

public class AnnotationConfigApplicationContext implements ApplicationContext {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationConfigApplicationContext.class);

    private final AnnotatedBeanDefinitionReader reader;
    private final ClassPathBeanDefinitionScanner scanner;
    private DefaultListableBeanFactory beanFactory;

    public AnnotationConfigApplicationContext() {
        this.beanFactory = new DefaultListableBeanFactory();
        this.reader = new AnnotatedBeanDefinitionReader(beanFactory);
        this.scanner = new ClassPathBeanDefinitionScanner(beanFactory);
    }

    public AnnotationConfigApplicationContext(Class<?>... annotatedClasses) {
        this();
        this.register(annotatedClasses);
        this.scan(annotatedClasses);
        this.refresh();
    }

    private void refresh() {
        beanFactory.instantiateBeans();
    }

    private void scan(Class<?>... annotatedClasses) {
        Object[] basePackages = BeanFactoryUtils.getBasePackages(annotatedClasses);
        scanner.scan(basePackages);
    }

    private void register(Class<?>... annotatedClasses) {
        reader.register(annotatedClasses);
    }

    @Override
    public String getApplicationName() {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return beanFactory.getBean(clazz);
    }

    @Override
    public Map<Class<?>, Object> getAnnotationTypeClass(Class<? extends Annotation> annotation) {
        return beanFactory.getAnnotationTypeClass(annotation);
    }

}
