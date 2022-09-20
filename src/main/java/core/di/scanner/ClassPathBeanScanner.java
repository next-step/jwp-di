package core.di.scanner;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.annotation.Repository;
import core.annotation.Service;
import core.annotation.web.Controller;
import core.di.factory.BeanFactory;

public class ClassPathBeanScanner {
	private static final Logger logger = LoggerFactory.getLogger(ClassPathBeanScanner.class);

	private static final List<Class<? extends Annotation>> ANNOTATIONS = List.of(Controller.class, Repository.class, Service.class);
	private final BeanFactory beanFactory;

	public  ClassPathBeanScanner(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public void scan(Object... basePackage) {
		Set<Class<?>> preInstantiateClazzSet = getPreInstantiateClazz(new Reflections(basePackage));
		preInstantiateClazzSet.stream().forEach(preInstantiateClazz -> logger.debug(preInstantiateClazz.getName()));
		beanFactory.putPreInstanticateBeans(preInstantiateClazzSet);
	}

	private Set<Class<?>> getPreInstantiateClazz(Reflections reflections) {
		return ANNOTATIONS.stream()
						  .map(reflections::getTypesAnnotatedWith)
						  .flatMap(Set::stream)
						  .collect(Collectors.toSet());
	}
}
