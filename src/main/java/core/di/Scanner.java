package core.di;

import java.util.Set;

public interface Scanner<T> {

    Set<T> scan(Object... basePackage);

}
