package next.context.annotation;

public interface AnnotationConfigRegistry {

    void register(Class<?>... componentClasses);

    void scan(Object... basePackages);

}
