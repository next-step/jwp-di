package core.di.scanner;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.springframework.beans.BeanUtils;

import core.annotation.Bean;
import core.annotation.Configuration;
import core.di.factory.BeanFactory;

public class ConfigurationBeanScanner {
	private final BeanFactory beanFactory;

	public ConfigurationBeanScanner(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public void scan(Object... basePackage) {
		Set<Class<?>> configureClassSet = getPreInstantiateClazz(new Reflections(basePackage));
		Map<Method, Object> configureBeanMethod = new HashMap<>();
		for (Class<?> configureClass : configureClassSet) {
			configureBeanMethod.putAll(getConfigurationBeanMethod(configureClass));
		}
		beanFactory.putConfigureBeans(configureBeanMethod);
	}

	private Set<Class<?>> getPreInstantiateClazz(Reflections reflections) {
		return reflections.getTypesAnnotatedWith(Configuration.class);
	}

	private Map<Method, Object> getConfigurationBeanMethod(Class<?> configurationClass) {
		Map<Method, Object> configureBeanMethod = new HashMap<>();
		try {
			Object instance = BeanUtils.instantiateClass(configurationClass);
			Method[] methods = configurationClass.getDeclaredMethods();
			Arrays.stream(methods)
				  .filter(m -> m.isAnnotationPresent(Bean.class))
				  .forEach(m -> configureBeanMethod.put(m, instance));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return configureBeanMethod;
	}
}
