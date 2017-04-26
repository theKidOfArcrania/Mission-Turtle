package turtle.comps;

import javafx.scene.layout.Pane;

public class Grid extends Pane
{
	private static final int DEF_CELL_SIZE = 100;
	
	private final int rows;
	private final int cols;
	private final int cellSize;
	
	/**
	 * Creates a new grid with the following dimensions
	 * @param rows the number of rows
	 * @param cols the number of columns
	 */
	public Grid(int rows, int cols)
	{
		cellSize = DEF_CELL_SIZE;
		this.rows = rows;
		this.cols = cols;
	}
	
	/**
	 * Gets the size of each cell
	 * @return the size of each cell in pixels
	 */
	public int getCellSize()
	{
		return cellSize;
	}
}
