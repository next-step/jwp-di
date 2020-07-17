package core.di;

import core.annotation.ComponentScan;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
public class ComponentBasePackageScanner implements Scanner<Object> {

    private static final String ANNOTATION_BASE_PACKAGE = "core.annotation";
    private static final Class<ComponentScan> COMPONENT_SCAN_ANNOTATION = ComponentScan.class;

    @Getter
    private Set<Object> basePackages = new HashSet<>();
    private Reflections wholeReflections = new Reflections("", new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner());

    public ComponentBasePackageScanner(Object... basePackage) {
        this.wholeReflections = new Reflections(basePackage, new TypeAnnotationsScanner(), new SubTypesScanner(), new MethodAnnotationsScanner()); // TODO: 2020/07/17 is really needed?
    }

    @Override
    public Set<Object> scan() {
        // TODO: 2020/07/17  annotation package 에서 ComponentScan을 상속받는 어노테이션 전부 찾기 (재귀)

        // TODO: 2020/07/17 ComponentScan 적용되어 있는 class 찾기 

        // TODO: 2020/07/17 ComponentScan Class 에서 해당 어노테이션의 basePackage 등록하기 

        // TODO: 2020/07/17 ComponentScan Class 에서 해당 어노테이션의 basePackage 가 없는 Class 들 뽑아내기
        // TODO: 2020/07/17 ComponentScan 을 상속받는 다른 어노테이션들을 달고 있는 Class 들 찾기 

        // TODO: 2020/07/17 위 두 경우를 합친 class 들에게서 위치하고 있는 package 값을 전부 등록한다.

        Set<Class<?>> classesAnnotatedComponentScan = wholeReflections.getTypesAnnotatedWith(COMPONENT_SCAN_ANNOTATION, true);

        Set<Class<?>> collect = classesAnnotatedComponentScan.stream()
                .map(this::registerBasePackage)
                .filter(clazz -> isEmptyBasePackages(clazz.getAnnotation(COMPONENT_SCAN_ANNOTATION)))
                .collect(Collectors.toSet());

        Set<Class<?>> classes = temp(wholeReflections, collect);

        Set<Object> wholeBasePackage = classes.stream()
                .map(this::findPackageOf)
                .flatMap(Arrays::stream)
                .collect(Collectors.toSet());

        if (wholeBasePackage.isEmpty()) {
            throw new IllegalStateException("Base package not initialized");
        }

        return wholeBasePackage;
    }

    Class<?> registerBasePackage(Class<?> clazz) {
        ComponentScan annotation = clazz.getAnnotation(COMPONENT_SCAN_ANNOTATION);
        if (isEmptyBasePackages(annotation)) {
            return clazz;
        }

        String[] basePackage = annotation.basePackage();
        this.basePackages.addAll(Arrays.asList(basePackage));
        return clazz;
    }

    private boolean isEmptyBasePackages(ComponentScan annotation) {
        return annotation == null || annotation.basePackage().length == 0;
    }

    // TODO: 2020/07/16 구현체의 package 찾기
    private Set<Class<?>> temp(Reflections reflections, Set<Class<?>> classes) {
        Set<Class<?>> componentScanClasses = new HashSet<>();
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotation()) {
                Set<Class<?>> scanned = temp(reflections, reflections.getTypesAnnotatedWith((Class<? extends Annotation>) clazz));
                componentScanClasses.addAll(scanned);
                continue;
            }

            componentScanClasses.add(clazz);
        }

        return componentScanClasses;
    }

    private String[] findPackageOf(Class<?> clazz) {
        return new String[]{clazz.getPackage().getName()};
    }

}
