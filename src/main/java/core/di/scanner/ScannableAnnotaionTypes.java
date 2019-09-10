package core.di.scanner;

import com.google.common.collect.ImmutableSet;
import core.annotation.Configuration;
import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;

import java.lang.annotation.Annotation;
import java.util.Set;

public class ScannableAnnotaionTypes {

    private static final Set<Class<? extends Annotation>> SCANNABLE_TYPES = ImmutableSet.of(
            Configuration.class, Controller.class, Service.class, Repository.class
    );

    public static Set<Class<? extends Annotation>> getAllTypes() {
        return ImmutableSet.copyOf(SCANNABLE_TYPES);
    }

    public static boolean isScannable(Class<?> clazz) {
        return SCANNABLE_TYPES.stream().anyMatch(clazz::isAnnotationPresent);
    }

}
