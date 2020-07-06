package study;

import java.lang.reflect.Method;

public class SayMethodMatcher implements MethodMatcher {
    private static final String PREFIX = "say";
    @Override
    public boolean matches(Method m, Class targetClass, Object[] args) {
        return m.getName().startsWith(PREFIX);
    }
}
