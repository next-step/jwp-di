package core.di.factory;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Method;
import java.util.Map;

class BeanMethodInitializerTestSupport {

    private static final Map<String, Method> METHODS = ImmutableMap.of(
            "noneParameterMethod", BeanUtils.findDeclaredMethod(BeanMethodInitializerTestSupport.class, "noneParameterMethod"),
            "oneParameterMethod", BeanUtils.findDeclaredMethod(BeanMethodInitializerTestSupport.class, "oneParameterMethod", String.class)
    );

    protected Method getMethod(String methodName) {
        return METHODS.get(methodName);
    }

    void noneParameterMethod() {}

    void oneParameterMethod(String str) {}

}