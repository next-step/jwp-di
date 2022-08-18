package core.mvc.tobe.support;

import core.mvc.tobe.MethodParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArgumentResolvers {
    private final List<ArgumentResolver> store = new ArrayList<>();


    public ArgumentResolvers addResolver(ArgumentResolver resolver) {
        store.add(resolver);
        return this;
    }

    public Optional<ArgumentResolver> findByMethodParameter(MethodParameter methodParameter) {
        return store.stream().filter(resolver-> resolver.supports(methodParameter))
                .findFirst();
    }
}
