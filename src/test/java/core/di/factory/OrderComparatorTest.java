package core.di.factory;

import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.springframework.core.annotation.OrderUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author KingCjy
 */
public class OrderComparatorTest {

    @Order(1)
    class Order1 {

    }

    @Order(2)
    class Order2 {

    }

    @Order(3)
    class Order3 {

    }

    @Test
    public void orderTest() {
        Object[] orders = new Object[] { new Order2(), new Order3(), new Order1()};
        AnnotationAwareOrderComparator.sort(orders);

        assertThat(orders[0].getClass()).isEqualTo(Order1.class);
        assertThat(orders[1].getClass()).isEqualTo(Order2.class);
        assertThat(orders[2].getClass()).isEqualTo(Order3.class);
    }

    @Test
    public void onStreamTest() {
        Object[] orders = new Object[] { new Order2(), new Order3(), new Order1()};

        Class<?>[] classes = Arrays.stream(orders)
                .sorted(AnnotationAwareOrderComparator.INSTANCE)
                .map(object -> object.getClass())
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .toArray(new Class[]{});

        assertThat(classes[0]).isEqualTo(Order1.class);
        assertThat(classes[1]).isEqualTo(Order2.class);
        assertThat(classes[2]).isEqualTo(Order3.class);
    }
}
