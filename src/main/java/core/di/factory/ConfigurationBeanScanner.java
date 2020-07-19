package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.MemberUsageScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.util.Set;

public class ConfigurationBeanScanner {

    public static Set<Class<?>> scan() {
        Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(""))
                .addScanners(new MemberUsageScanner(), new MethodAnnotationsScanner(), new MethodParameterScanner()));
        Set<Class<?>> classes = getTypesAnnotatedWith(reflections, Configuration.class);

        return classes;
    }

    private static Set<Class<?>> getTypesAnnotatedWith(Reflections reflections, Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = Sets.newHashSet();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }
}
