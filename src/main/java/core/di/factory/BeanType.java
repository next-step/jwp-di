package core.di.factory;

import core.annotation.*;
import core.annotation.web.Controller;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum BeanType {
    COMPONENT(Component.class),
    CONTROLLER(Controller.class),
    SERVICE(Service.class),
    REPOSITORY(Repository.class),
    CONFIGURATION(Configuration.class),
    BEAN(Bean.class);

    private static final Map<Class<? extends Annotation>, BeanType> BEAN_TYPES =
            Arrays.stream(values())
                    .collect(Collectors.toMap(BeanType::getAnnotation, Function.identity()));

    private final Class<? extends Annotation> annotation;

    BeanType(final Class<? extends Annotation> annotation) {
        this.annotation = annotation;
    }

    public static BeanType of(Class<? extends Annotation> annotation) {
        return BEAN_TYPES.get(annotation);
    }

    public boolean isComponentType() {
        return this == COMPONENT ||
                this == CONTROLLER ||
                this == SERVICE ||
                this == REPOSITORY;
    }

    public Class<? extends Annotation> getAnnotation() {
        return annotation;
    }
}
