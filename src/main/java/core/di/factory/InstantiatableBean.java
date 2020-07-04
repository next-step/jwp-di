package core.di.factory;

import java.util.List;

public interface InstantiatableBean {
    Object instantiate(List<Object> dependencies);
}
