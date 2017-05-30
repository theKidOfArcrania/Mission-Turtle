package turtle.ui;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * EqualGridPane.java
 * This layout type will layout all its children into equal cell sizes in
 * a grid-like formation.
 *
 * @author Henry
 *         Date: 5/8/17
 *         Period: 2
 */
public class EqualGridPane extends Pane
{
    private final int rows;
    private final int columns;
    private double hgap;
    private double vgap;
    
    /**
     * Creates an EqualGridPane with number of rows and columns.
     *
     * @param rows    number of rows
     * @param columns number of columns
     */
    public EqualGridPane(int rows, int columns)
    {
        if (rows < 0 || columns < 0)
            throw new IllegalArgumentException("Rows and columns must be " +
                    "non-negative");
        this.rows = rows;
        this.columns = columns;

        hgap = 0;
        vgap = 0;
    }

    /**
     * This determines the optimal size to set based on the premise that
     * the size must be between min and max, and should optimally be preferred
     * size.
     *
     * @param min  the minimum bound
     * @param pref the preferred size
     * @param max  the maximum bound
     * @return optimal size.
     */
    private static double boundedSize(double min, double pref, double max)
    {
        double a = Math.max(pref, min);
        double b = Math.max(min, max);
        return Math.min(a, b);
    }

    /**
     * @return horizontal gap between cells.
     */
    public double getHgap()
    {
        return hgap;
    }

    /**
     * @param hgap new horizontal gap between cells.
     */
    public void setHgap(double hgap)
    {
        this.hgap = hgap;
    }

    /**
     * @return vertical gap between cells.
     */
    public double getVgap()
    {
        return vgap;
    }

    /**
     * @param vgap new vertical gap between cells
     */
    public void setVgap(double vgap)
    {
        this.vgap = vgap;
    }

    /**
     * Layouts all the children within this grid
     */
    @Override
    protected void layoutChildren()
    {
        int count = getManagedChildren().size();
        int rows = this.rows <= 1 ? 1 : this.rows;
        int cols = count / rows;
        if (columns > cols)
            cols = columns;
        else if (cols * rows != count)
            cols++;

        Insets margin = getInsets();
        double width = getWidth() - margin.getLeft() - margin.getRight() -
                hgap * (count - 1);
        double height = getHeight() - margin.getTop() - margin.getBottom() -
                vgap * (count - 1);

        if (width <= 0 || height <= 0)
            for (int j = 0; j < count; j++)
                layoutInArea(getManagedChildren().get(j), 0, 0, 0, 0, 0,
                        HPos.CENTER, VPos.CENTER);
        else
        {
            width /= cols;
            height /= rows;
            for (int j = 0; j < count; j++)
            {
                int r = j / cols;
                int c = j - r * cols;
                layoutInArea(getManagedChildren().get(j), c * (hgap + width),
                        r * (vgap + height), width, height, 0, HPos.CENTER,
                        VPos.CENTER);
            }
        }
    }

    /**
     * Compute maximum height
     *
     * @param width width to compare with
     * @return size of height
     */
    @Override
    protected double computeMaxHeight(double width)
    {
        double cellHeight = 0;
        int count = getManagedChildren().size();
        for (int i = 0; i < count; i++)
        {
            Node node = getManagedChildren().get(i);
            cellHeight = Math.max(cellHeight, node.maxHeight(-1));
        }
        if (cellHeight == 0)
            return getInsets().getTop() + getInsets().getBottom();
        else
        {
            int rows = this.rows <= 1 ? 1 : this.rows;
            return cellHeight * rows + vgap * (rows - 1) + getInsets().getTop()
                    + getInsets().getBottom();
        }
    }

    /**
     * Compute maximum width
     *
     * @param height height to compare with
     * @return size of width
     */
    @Override
    protected double computeMaxWidth(double height)
    {
        double cellWidth = 0;
        for (int i = 0; i < getManagedChildren().size(); i++)
        {
            Node node = getManagedChildren().get(i);
            if (node.isManaged())
                cellWidth = Math.max(cellWidth, node.maxWidth(-1));
        }
        return computeWidth(cellWidth);
    }

    /**
     * Compute minimum height
     *
     * @param width width to compare with
     * @return size of height
     */
    @Override
    protected double computeMinHeight(double width)
    {
        double cellHeight = 0;
        int count = getManagedChildren().size();
        for (int i = 0; i < count; i++)
        {
            Node node = getManagedChildren().get(i);
            cellHeight = Math.max(cellHeight, node.minHeight(-1));
        }
        if (cellHeight == 0)
            return getInsets().getTop() + getInsets().getBottom();
        else
        {
            int rows = this.rows <= 1 ? 1 : this.rows;
            return cellHeight * rows + vgap * (rows - 1) + getInsets().getTop()
                    + getInsets().getBottom();
        }
    }

    /**
     * Compute minimum width
     *
     * @param height height to compare with
     * @return size of width
     */
    @Override
    protected double computeMinWidth(double height)
    {
        double cellWidth = 0;
        int count = getManagedChildren().size();
        for (int i = 0; i < count; i++)
        {
            Node node = getManagedChildren().get(i);
            if (node.isManaged())
                cellWidth = Math.max(cellWidth, node.minWidth(-1));
        }
        return computeWidth(cellWidth);
    }

    /**
     * Compute preferred height
     *
     * @param width width to compare with
     * @return size of height
     */
    @Override
    protected double computePrefHeight(double width)
    {
        double cellHeight = 0;
        int count = getManagedChildren().size();
        for (int i = 0; i < count; i++)
        {
            Node node = getManagedChildren().get(i);
            cellHeight = Math.max(cellHeight, boundedSize(node.prefHeight(-1),
                    node.minHeight(-1), node.maxHeight(-1)));
        }
        if (cellHeight == 0)
            return getInsets().getTop() + getInsets().getBottom();
        else
        {
            int rows = this.rows <= 1 ? 1 : this.rows;
            return cellHeight * rows + vgap * (rows - 1) + getInsets().getTop()
                    + getInsets().getBottom();
        }
    }

    /**
     * Compute preferred width
     *
     * @param height height to compare with
     * @return size of width
     */
    @Override
    protected double computePrefWidth(double height)
    {
        double cellWidth = 0;
        int count = getManagedChildren().size();
        for (int i = 0; i < count; i++)
        {
            Node node = getManagedChildren().get(i);
            cellWidth = Math.max(cellWidth, boundedSize(node.prefWidth(-1),
                    node.minWidth(-1), node.maxWidth(-1)));
        }
        return computeWidth(cellWidth);
    }

    /**
     * Computes a total width based on the per cell width
     * @param cellWidth the cell width
     * @return total width in pixels
     */
    private double computeWidth(double cellWidth)
    {
        int count = getManagedChildren().size();
        if (cellWidth == 0)
            return getInsets().getLeft() + getInsets().getRight();
        else
        {
            int rows = this.rows <= 1 ? 1 : this.rows;
            int cols = count / rows;
            if (columns > cols)
                cols = columns;
            else if (cols * rows != count)
                cols++;
            return cellWidth * cols + hgap * (cols - 1) + getInsets().getLeft()
                    + getInsets().getRight();
        }
    }
}
