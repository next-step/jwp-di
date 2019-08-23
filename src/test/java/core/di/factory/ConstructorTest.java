package core.di.factory;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNull;

public class ConstructorTest {

    @Test
    void newInstance() throws IllegalAccessException, InvocationTargetException, InstantiationException {

        Constructor<?> carConstructor = Car.class.getConstructors()[0];
        Object[] args = new Object[2];
        final Car car = (Car) carConstructor.newInstance(args);
        assertNull(car.name);
        assertNull(car.engine);
    }

    public static class Car {

        public String name;
        public Engine engine;

        public Car(Engine engine, String name) {
            this.engine = engine;
            this.name = name;
        }
    }

    public static class Engine {
        String name;
    }

}
