package core.di.factory;

import com.google.common.collect.Sets;
import core.annotation.ComponentScan;
import core.di.beans.definition.reader.AnnotatedBeanDefinitionReader;
import core.di.beans.definition.reader.ClasspathBeanDefinitionReader;
import core.mvc.tobe.HandlerExecution;
import core.mvc.tobe.HandlerKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static java.util.stream.Collectors.toSet;

@Slf4j
public class ApplicationContext {
    private final BeanFactory beanFactory;

    public ApplicationContext(Class<?> configurationClass) {
        this(new Class[] {configurationClass}, (String[]) null);
    }

    public ApplicationContext(Class<?>... configurationClasses) {
        this(configurationClasses, (String[]) null);
    }

    public ApplicationContext(Class<?> configurationClass, String ...basePackages) {
        this(new Class[] {configurationClass}, basePackages);
    }

    public ApplicationContext(Class<?>[] configurationClasses, String ...basePackages) {
        Set<String> mergedBasePackages = getBasePackages(configurationClasses);
        beanFactory = new BeanFactory();

        if (Objects.nonNull(configurationClasses)) {
            AnnotatedBeanDefinitionReader beanDefinitionReader = new AnnotatedBeanDefinitionReader(beanFactory);
            beanDefinitionReader.read(configurationClasses);
        }

        if (!ArrayUtils.isEmpty(basePackages)) {
            mergedBasePackages.addAll(new HashSet<>(Arrays.asList(basePackages)));
        }

        if (!CollectionUtils.isEmpty(mergedBasePackages)) {
            ClasspathBeanDefinitionReader beanDefinitionReader = new ClasspathBeanDefinitionReader(beanFactory);
            beanDefinitionReader.doScan(mergedBasePackages.toArray());
        }

        beanFactory.instantiateBeans();
    }

    private Set<String> getBasePackages(Class<?>[] configurationClasses) {
        if (ArrayUtils.isEmpty(configurationClasses)) {
            return Sets.newHashSet();
        }

        return Arrays.stream(configurationClasses)
            .map(configClass -> configClass.getAnnotation(ComponentScan.class))
            .filter(componentScan -> Objects.nonNull(componentScan) && !ArrayUtils.isEmpty(componentScan.value()))
            .flatMap(componentScan -> Arrays.stream(componentScan.value()))
            .peek(basePackage -> log.debug("Found BasePackage: {}", basePackage))
            .collect(toSet());
    }

    public Map<HandlerKey, HandlerExecution> scan() {
        HandlerBeanScanner handlerBeanScanner = new HandlerBeanScanner(beanFactory);
        return handlerBeanScanner.scan();
    }

    public <T> T getBean(Class<T> requiredType) {
        return beanFactory.getBean(requiredType);
    }
}
