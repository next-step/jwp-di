package core.annotation;

import core.di.Scanner;
import lombok.RequiredArgsConstructor;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AnnotationScanner implements Scanner<Class<? extends Annotation>> {

    private static final String ANNOTATION_BASE_PACKAGE = "core.annotation";

    private final Class<? extends Annotation> target;
    private final Reflections annotationReflections = new Reflections(ANNOTATION_BASE_PACKAGE);

    @Override
    public Set<Class<? extends Annotation>> scan() {
        return findAnnotationsAnnotatedBy(target);
    }

    private Set<Class<? extends Annotation>> findAnnotationsAnnotatedBy(Class<? extends Annotation> annotation) {
        Set<Class<?>> annotations = annotationReflections.getTypesAnnotatedWith(annotation);
        if (annotations.isEmpty()) {
            return new HashSet<>(Collections.singletonList(annotation));
        }

        Set<Class<? extends Annotation>> scannedAnnotations = annotations.stream()
                .map(clazz -> findAnnotationsAnnotatedBy((Class<? extends Annotation>) clazz))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        scannedAnnotations.add(annotation);
        return scannedAnnotations;
    }

}
