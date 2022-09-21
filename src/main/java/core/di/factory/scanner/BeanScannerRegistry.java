package core.di.factory.scanner;

import java.util.ArrayList;
import java.util.List;

public class BeanScannerRegistry {
    private List<BeanScanner> beanScanners = new ArrayList<>();

    public void addBeanScanner(BeanScanner beanScanner) {
        beanScanners.add(beanScanner);
    }

    public void scan(Class<?>[] configurations) {
        beanScanners.forEach(
                beanScanner -> beanScanner.doScan(configurations));
    }
}
