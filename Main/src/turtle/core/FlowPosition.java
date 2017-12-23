package turtle.core;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * @author Henry Wang
 */
public class FlowPosition {
    // moveByProperty
    private final DoubleProperty moveByProperty = new SimpleDoubleProperty(this, "moveBy");
    public final DoubleProperty moveByProperty() {
       return moveByProperty;
    }
    public final double getMoveBy() {
       return moveByProperty.get();
    }
    public final void setMoveBy(double value) {
        moveByProperty.set(value);
    }


}
