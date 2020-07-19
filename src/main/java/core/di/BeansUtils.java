package core.di;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iltaek on 2020/07/19 Blog : http://blog.iltaek.me Github : http://github.com/iltaek
 */
public class BeansUtils {

    public static List<Object> getParameterObjects(Class<?>[] parameterTypes, PreparePrams pp) {
        List<Object> params = new ArrayList<>();
        for (Class<?> parameterType : parameterTypes) {
            params.add(pp.setParam(parameterType));
        }
        return params;
    }

    @FunctionalInterface
    public interface PreparePrams {

        Object setParam(Class<?> parameterType);
    }
}
