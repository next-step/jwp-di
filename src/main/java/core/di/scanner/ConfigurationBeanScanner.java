package core.di.scanner;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.annotation.Bean;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;

public class ConfigurationBeanScanner {
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationBeanScanner.class);
	private final BeanFactory beanFactory;

	public ConfigurationBeanScanner(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public void scan(Object... basePackage) {
		Set<Class<?>> configureClassSet = getPreInstantiateClazz(new Reflections(basePackage));
		configureClassSet.stream().forEach(preInstantiateClazz -> logger.debug(preInstantiateClazz.getName()));
		Map<Class<?>, Method> beanMethods = getBeanMethods(configureClassSet);
		beanFactory.putInstanticateBeanMethods(beanMethods);
	}

	private Set<Class<?>> getPreInstantiateClazz(Reflections reflections) {
		return reflections.getTypesAnnotatedWith(Configuration.class);
	}

	private Map<Class<?>, Method> getBeanMethods(Set<Class<?>> configureClassSet) {
		Map<Class<?>, Method> beanMethods = new HashMap<>();
		for (Class<?> configureClass : configureClassSet) {
			Map<Class<?>, Method> result = Arrays.stream(configureClass.getDeclaredMethods())
												 .map(Arrays::asList)
												 .flatMap(Collection::stream)
												 .filter(method -> method.isAnnotationPresent(Bean.class))
												 .collect(Collectors.toMap(Method::getReturnType, Function.identity()));
			beanMethods.putAll(result);
		}
		return beanMethods;
	}

}
