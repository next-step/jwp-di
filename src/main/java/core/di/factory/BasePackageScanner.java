package core.di.factory;

import core.annotation.ComponentScan;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class BasePackageScanner {
    private List<String> basePackage = new ArrayList<>();

    public BasePackageScanner() {
        Reflections reflections = new Reflections("", new TypeAnnotationsScanner(), new SubTypesScanner());
        Set<Class<?>> classes = ScannerUtils.getTypesAnnotatedWith(reflections, ComponentScan.class);
        for (Class<?> clazz : classes) {
            String[] values = clazz.getAnnotation(ComponentScan.class).value();
            basePackage.addAll(Arrays.asList(values));
        }
    }

    public Object[] getBasePackage() {
        return this.basePackage.toArray(new Object[basePackage.size()]);
    }
}
