/**
 * GridView.java
 * 
 * Displays only a portion of the grid --- the portion that the player can
 * see at one time.
 * 
 * @author Henry Wang
 * Date: 4/27/17
 * Period: 2
 */

package turtle.core;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import turtle.comp.Player;

public class GridView extends Pane
{
	private static final double CORNER_RADIUS = 5.0;
	public static final int VIEW_ROWS = 8;
	public static final int VIEW_COLS = 12;
	
	private Grid viewed;
	private final int rows;
	private final int cols;
	
	/**
	 * Constructs a GridView.
	 * @param init the initial grid to view
	 */
	public GridView(Grid init)
	{
		rows = VIEW_ROWS;
		cols = VIEW_COLS;
		initGrid(init);
		
		LinearGradient grad = new LinearGradient(0, 0, 1, 1, true, null, 
				new Stop(0, Color.LIGHTGRAY), new Stop(1, Color.GRAY));
		setBackground(new Background(new BackgroundFill(grad, 
        		null, null)));
	}
	
	/**
	 * Initializes this GridView with another grid,
	 * cleaning up the previous grid's stuff.
	 * @param grid the grid to initialize with.
	 */
	public void initGrid(Grid grid)
	{
		viewed = grid;
		getChildren().clear();
		
		int cellSize = Grid.DEF_CELL_SIZE;
		if (grid != null)
			cellSize = grid.getCellSize();
		
		Rectangle clip = new Rectangle(0, 0, cellSize * cols, cellSize * rows);
		clip.setArcHeight(CORNER_RADIUS);
		clip.setArcWidth(CORNER_RADIUS);
		setClip(clip);
		
		if (grid != null)
			getChildren().add(grid);
		layoutChildren();
		updatePos();
	}
	
	/**
	 * @return the grid viewed by this grid-view.
	 */
	public Grid getGrid()
	{
		return viewed;
	}
	
	/**
	 * Updates a frame, propagating it to grid.
	 * @param frame the frame number
	 */
	public void updateFrame(long frame)
	{
		if (viewed == null)
			return;
		
		viewed.updateFrame(frame);
		updatePos();
	}
	
	/**
	 * Computes maximum width.
	 * @param height height to compare with (can be -1).
	 * @return the value in pixels
	 */
	@Override
	protected double computeMaxWidth(double height)
	{
		if (viewed == null)
			return cols * Grid.DEF_CELL_SIZE;
		return cols * viewed.getCellSize();
	}
	
	/**
	 * Computes maximum height.
	 * @param width width to compare with (can be -1).
	 * @return the value in pixels
	 */
	@Override
	protected double computeMaxHeight(double width)
	{
		if (viewed == null)
			return rows * Grid.DEF_CELL_SIZE;
		return rows * viewed.getCellSize();
	}
	
	/**
	 * Computes preferred width.
	 * @param height height to compare with (can be -1).
	 * @return the value in pixels
	 */
	@Override
	protected double computePrefWidth(double height)
	{
		if (viewed == null)
			return cols * Grid.DEF_CELL_SIZE;
		return cols * viewed.getCellSize();
	}
	
	/**
	 * Computes preferred height.
	 * @param width width to compare with (can be -1).
	 * @return the value in pixels
	 */
	@Override
	protected double computePrefHeight(double width)
	{
		if (viewed == null)
			return rows * Grid.DEF_CELL_SIZE;
		return rows * viewed.getCellSize();
	}
	
	/**
	 * Computes minimum width.
	 * @param height height to compare with (can be -1).
	 * @return the value in pixels
	 */
	@Override
	protected double computeMinWidth(double height)
	{
		if (viewed == null)
			return cols * Grid.DEF_CELL_SIZE;
		return cols * viewed.getCellSize();
	}
	
	/**
	 * Computes minimum height.
	 * @param width width to compare with (can be -1).
	 * @return the value in pixels
	 */
	@Override
	protected double computeMinHeight(double width)
	{
		if (viewed == null)
			return rows * Grid.DEF_CELL_SIZE;
		return rows * viewed.getCellSize();
	}
	
	/**
	 * Layouts all the children in this GridView.
	 */
	@Override
	protected void layoutChildren()
	{
		if (viewed == null)
			return;
		
		double width = cols * viewed.getCellSize();
		double height = rows * viewed.getCellSize();
		layoutInArea(viewed, 0, 0, width, height, 0, HPos.CENTER, VPos.CENTER);
	}
	
	/**
	 * Calculates the viewport offset so that the viewport contains specified
	 * point, and viewport does not go out of bounds.
	 * @param viewSize the view port size.
	 * @param maxSize the max bound size.
	 * @param point the point offset
	 * @return an offset of the viewport to fit condition
	 */
	private double calcOffset(double viewSize, double maxSize, double point)
	{
		double off = point - viewSize / 2;
		return Math.min(Math.max(0, off), maxSize - viewSize);
	}
	
	/**
	 * Updates the grid translate offset to follow player.
	 */
	private void updatePos()
	{
		if (viewed == null)
			return;
		
		Player p = viewed.getPlayer();
		if (p == null)
			return;
		
		double cell = viewed.getCellSize();
		viewed.setTranslateX(-calcOffset(viewed.getWidth(), 
				viewed.getColumns() * cell, p.getTranslateX()));
		viewed.setTranslateY(-calcOffset(viewed.getHeight(), 
				viewed.getRows() * cell,  p.getTranslateY()));
	}

	/**
	 * Delegate method that queries the interactable player.
	 * @return the player of the grid contained inside this grid-view.
	 */
	public Player getPlayer()
	{
		if (viewed == null)
			return null;
		return viewed.getPlayer();
	}
}
