package core.di.factory;

import core.annotation.*;
import core.annotation.web.Controller;

import java.lang.annotation.Annotation;

public enum BeanType {
    COMPONENT(Component.class),
    CONTROLLER(Controller.class),
    SERVICE(Service.class),
    REPOSITORY(Repository.class),
    CONFIGURATION(Configuration.class),
    BEAN(Bean.class);

    private final Class<? extends Annotation> annotation;

    BeanType(final Class<? extends Annotation> annotation) {
        this.annotation = annotation;
    }

    public boolean isComponentType() {
        return this == COMPONENT ||
                this == CONTROLLER ||
                this == SERVICE ||
                this == REPOSITORY;
    }
}
