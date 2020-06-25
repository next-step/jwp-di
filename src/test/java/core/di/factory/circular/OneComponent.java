package core.di.factory.circular;

import core.annotation.Component;
import core.annotation.Inject;

@Component
public class OneComponent {
    private TwoComponent twoComponent;

    @Inject
    public OneComponent(TwoComponent twoComponent) {
        this.twoComponent = twoComponent;
    }
}
