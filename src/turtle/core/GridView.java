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
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import turtle.comp.Player;

public class GridView extends Pane
{
	public static final int VIEW_ROWS = 10;
	public static final int VIEW_COLS = 15;
	
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
	}
	
	/**
	 * Initializes this GridView with another grid,
	 * cleaning up the previous grid's stuff.
	 * @param grid the grid to initialize with.
	 * @throws NullPointerException if grid is null.
	 */
	public void initGrid(Grid grid)
	{
		if (grid == null)
			throw new NullPointerException();
		
		viewed = grid;
		
		int cellSize = grid.getCellSize();
		Rectangle clip = new Rectangle(0, 0, cellSize * cols, cellSize * rows);
		setClip(clip);
		
		getChildren().clear();
		getChildren().add(grid);
		layoutChildren();
		updatePos();
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
		return rows * viewed.getCellSize();
	}
	
	/**
	 * Layouts all the children in this GridView.
	 */
	@Override
	protected void layoutChildren()
	{
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
		Player p = viewed.getPlayer();
		if (p == null)
			return;
		
		double cell = viewed.getCellSize();
		viewed.setTranslateX(calcOffset(cols * cell, viewed.getWidth(), 
				p.getTranslateX()));
		viewed.setTranslateX(calcOffset(rows * cell, viewed.getHeight(), 
				p.getTranslateY()));
	}
}
