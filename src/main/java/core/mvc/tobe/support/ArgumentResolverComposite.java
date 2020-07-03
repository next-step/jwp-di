package core.mvc.tobe.support;

import core.annotation.Component;
import core.di.factory.BeanFactory;
import core.mvc.tobe.MethodParameter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author KingCjy
 */
public class ArgumentResolverComposite implements ArgumentResolver {

    private Set<ArgumentResolver> argumentResolvers;
    private BeanFactory beanFactory;

    public ArgumentResolverComposite(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;

        initArgumentResolvers();
    }

    private void initArgumentResolvers() {
        argumentResolvers = Arrays.stream(beanFactory.getAnnotatedBeans(Component.class))
                .filter(object -> ArgumentResolver.class.isAssignableFrom(object.getClass()))
                .map(object -> (ArgumentResolver) object)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public boolean supports(MethodParameter methodParameter) {
        return getArgumentResolver(methodParameter).isPresent();
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, HttpServletRequest request, HttpServletResponse response) {
        return getArgumentResolver(methodParameter)
                .map(resolver -> resolver.resolveArgument(methodParameter, request, response))
                .orElse(null);
    }

    private Optional<ArgumentResolver> getArgumentResolver(MethodParameter methodParameter) {
        return argumentResolvers.stream()
                .filter(argumentResolver -> argumentResolver.supports(methodParameter))
                .findAny();
    }
}
