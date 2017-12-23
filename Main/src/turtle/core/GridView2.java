package turtle.core;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.layout.Region;

import java.util.HashMap;

/**
 * @author Henry Wang
 */
public class GridView2 extends Region {
    private final HashMap<CellView, Node> visible;
    private final HashMap<CellView, FlowPosition> posStatic;
    private final HashMap<CellView, FlowPosition> posNonstatic;


    public GridView2() {
        visible = new HashMap<>();
        posStatic = new HashMap<>();
        posNonstatic = new HashMap<>();
    }

    public void addCellView(CellView v) {

    }

    public void removeCellView(CellView v) {

    }

    public void updateView() {

    }

    // Javafx Properties
    // viewXProperty
    private final DoubleProperty viewXProperty = new SimpleDoubleProperty(this, "viewX");
    public final DoubleProperty viewXProperty() {
       return viewXProperty;
    }
    public final double getViewX() {
       return viewXProperty.get();
    }
    public final void setViewX(double value) {
        viewXProperty.set(value);
    }

    // viewYProperty
    private final DoubleProperty viewYProperty = new SimpleDoubleProperty(this, "viewY");
    public final DoubleProperty viewYProperty() {
       return viewYProperty;
    }
    public final double getViewY() {
       return viewYProperty.get();
    }
    public final void setViewY(double value) {
        viewYProperty.set(value);
    }


}
