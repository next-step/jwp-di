package core.di.factory.circular;

import core.annotation.Component;
import core.annotation.Inject;

@Component
public class ThreeComponent {
    private OneComponent oneComponent;

    @Inject
    public ThreeComponent(OneComponent oneComponent) {
        this.oneComponent = oneComponent;
    }
}
