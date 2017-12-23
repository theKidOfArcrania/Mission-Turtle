package turtle.core;

import javafx.scene.Node;

/**
 * @author Henry Wang
 */
public interface CellView {
    Node createNode(FlowPosition pos);

    /**
     * Determines whether if this cell view is a static, i.e. does not move. If the CellView is a
     * static element, some optimizations can be made for this.
     * @return true if static, false if not static
     */
    boolean isStatic();
}
