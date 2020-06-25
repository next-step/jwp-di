package core.di.factory.circular;

import core.annotation.Component;
import core.annotation.Inject;

@Component
public class TwoComponent {
    private ThreeComponent threeComponent;

    @Inject
    public TwoComponent(ThreeComponent threeComponent) {
        this.threeComponent = threeComponent;
    }
}
