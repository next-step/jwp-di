package core.mvc.tobe;

import com.google.common.collect.Sets;
import core.mvc.tobe.support.ArgumentResolver;
import core.mvc.tobe.support.HttpRequestArgumentResolver;
import core.mvc.tobe.support.HttpResponseArgumentResolver;
import core.mvc.tobe.support.ModelArgumentResolver;
import core.mvc.tobe.support.PathVariableArgumentResolver;
import core.mvc.tobe.support.RequestParamArgumentResolver;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by iltaek on 2020/07/08 Blog : http://blog.iltaek.me Github : http://github.com/iltaek
 */
public class ArgumentResolverComposite {

    private final Set<ArgumentResolver> argumentResolvers;

    public ArgumentResolverComposite() {
        argumentResolvers = Sets.newHashSet(
            new HttpRequestArgumentResolver(),
            new HttpResponseArgumentResolver(),
            new RequestParamArgumentResolver(),
            new PathVariableArgumentResolver(),
            new ModelArgumentResolver()
        );
    }

    public ArgumentResolverComposite(Set<ArgumentResolver> argumentResolvers) {
        this.argumentResolvers = argumentResolvers;
    }

    public void addArgumentResolver(ArgumentResolver argumentResolver) {
        argumentResolvers.add(argumentResolver);
    }

    public Object resolveArgument(MethodParameter methodParameter, HttpServletRequest request, HttpServletResponse response) {
        for (ArgumentResolver resolver : argumentResolvers) {
            if (resolver.supports(methodParameter)) {
                return resolver.resolveArgument(methodParameter, request, response);
            }
        }

        throw new IllegalStateException("No suitable resolver for argument: " + methodParameter.getType());
    }
}
