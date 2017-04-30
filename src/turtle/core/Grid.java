/**
 * Grid.java
 * 
 * Manages and displays all the grid components in the level.
 * @author Henry Wang
 * Date: 4/27/17
 * Period: 2
 */

package turtle.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import turtle.comp.Player;

public class Grid extends Pane
{
	/**
	 * Manages a list of maze components and lays them out with appropriate
	 * sizes and locations. 
	 * 
	 * @author Henry
	 */
	private class ComponentPane extends Pane {
		/**
		 * Lays all the children components of this layer.
		 */
		@Override
		protected void layoutChildren()
		{
			for (Node child : getManagedChildren())
			{
				layoutInArea(child, 0, 0, cellSize, cellSize, 0, 
						HPos.CENTER, VPos.CENTER);
			}
		}
	}
	
	public static final int DEF_CELL_SIZE = 100;
	
	private final int rows;
	private final int cols;
	private final int cellSize;
	
	private final Cell[][] base;
	private final HashMap<Actor, Location> actorLocs;
	
	private Pane pnlBase;
	private Pane pnlStage;
	
	private Player player;
	
	/**
	 * Creates a new grid with the following dimensions
	 * @param rows the number of rows
	 * @param cols the number of columns
	 */
	public Grid(int rows, int cols)
	{
		cellSize = Component.DEFAULT_SET.getFrameSize();
		
		this.rows = rows;
		this.cols = cols;
		
		base = new Cell[rows][cols];
		actorLocs = new HashMap<>();
		
		pnlBase = new ComponentPane();
		pnlStage = new ComponentPane();
		getChildren().addAll(pnlBase, pnlStage);
	}
	
	/**
	 * Gets cell at location.
	 * @param row the row of the cell
	 * @param col the column of the cell
	 * @return the cell at the row/col position.
	 */
	public Cell getCellAt(int row, int col)
	{
		return base[row][col];
	}
	
	/**
	 * Gets cell at location.
	 * @param loc the location specifying row, column of cell
	 * @return the cell at the row/col position.
	 */
	public Cell getCellAt(Location loc)
	{
		return base[loc.getRow()][loc.getColumn()];
	}
	
	/**
	 * Gets the size of each cell
	 * @return the size of each cell in pixels
	 */
	public int getCellSize()
	{
		return cellSize;
	}

	/**
	 * @return the number of columns
	 */
	public int getColumns()
	{
		return cols;
	}
	
	/**
	 * Gets the player object of this level.
	 * @return the player.
	 */
	public Player getPlayer()
	{
		return player;
	}
	
	/**
	 * Gets a list of all residents within a particular location
	 * @param visitor actor to get relative dominance levels to
	 * @param row row of location
	 * @param col column of location
	 * @return a sorted list (by reverse DominanceLevel). 
	 */
	public List<Actor> getResidents(Actor visitor, int row, int col)
	{
		Location search = new Location(row, col);
		List<Actor> residents = new ArrayList<>();
		for (Node n : pnlStage.getChildren())
		{
			if (n instanceof Actor)
			{
				Actor a = (Actor)n;
				if (a.getHeadLocation().equals(search) || 
						a.getTrailingLocation().equals(search))
					residents.add(a);
			}
		}
		
		/**
		 * Compares two actors in reverse dominance order.
		 */
		Collections.sort(residents, new Comparator<Actor>() 
		{
			/**
			 * Compares each actor 
			 * @param a1 first actor
			 * @param a2 second actor
			 * @return negative for "less than", 0 for equals, positive 
			 * 	for "more than".
			 */
			@Override
			public int compare(Actor a1, Actor a2)
			{
				return a2.dominanceLevelFor(visitor).compareTo(
						a1.dominanceLevelFor(visitor));
			}
			
		});
		return residents;
	}
	
	/**
	 * @return the number of rows
	 */
	public int getRows()
	{
		return rows;
	}
	
	/**
	 * Moves the actor to a new location.
	 * @param comp actor to move
	 * @param row the new row to move to.
	 * @param col the new column to move to.
	 * @return true if and only if the actor moved
	 */
	public boolean moveActor(Actor comp, int row, int col)
	{
		return checkVisit(comp, row, col);
	}
	
	/**
	 * Places an actor into the grid.
	 * @param comp the component to place into grid.
	 * @return true if placed, false if rejected.
	 */
	public boolean placeActor(Actor comp)
	{
		if (comp.getParentGrid() == null)
			return false;
		if (actorLocs.containsKey(comp))
			return false;
		
		Location loc = comp.getHeadLocation();
		boolean success = checkVisit(comp, loc.getRow(), loc.getColumn());
		if (success)
		{
			if (comp instanceof Player)
				player = (Player)comp;
			
			comp.setParentGrid(this);
			comp.getTrailingLocation().setLocation(loc);
			comp.setTranslateX(loc.getColumn() * cellSize);
			comp.setTranslateY(loc.getRow() * cellSize);
			
			List<Node> children = pnlStage.getChildren();
			DominanceLevel test = comp.dominanceLevelFor(null);
			for (int i = 0; i < children.size(); i++)
			{
				if (children.get(i) instanceof Actor)
				{
					Actor child = (Actor) children.get(i);
					if (child.dominanceLevelFor(null).compareTo(test) > 0)
						break;
				}
			}
			actorLocs.put(comp, loc);
		}
		return success;
	}
	
	/**
	 * Places a new cell in the cell's specified location.
	 * @param comp new cell to put.
	 * @return true if it is placed, false otherwise.
	 */
	public boolean placeCell(Cell comp)
	{
		if (comp.getParentGrid() == null)
			return false;
		if (pnlBase.getChildren().contains(comp))
			return false;
		
		Location loc = comp.getHeadLocation();
		if (base[loc.getRow()][loc.getColumn()] != null)
			return false;
		
		comp.setParentGrid(this);
		base[loc.getRow()][loc.getColumn()] = comp;
		pnlBase.getChildren().add(comp);
		return true;
	}
	
	/**
	 * Removes an actor from the grid.
	 * @param comp the actor to remove.
	 * @return true if and only if this call resulted in a change of the grid.
	 */
	public boolean removeActor(Actor comp)
	{
		if (actorLocs.containsKey(comp))
		{
			if (comp == player)
				player = null;
			
			comp.setParentGrid(null);
			pnlStage.getChildren().remove(comp);
			actorLocs.remove(comp);
			return true;
		}
		return false;
	}
	
	/**
	 * Removes an cell from the grid.
	 * @param comp the cell to remove.
	 * @return true if and only if this call resulted in a change of the grid.
	 */
	public boolean removeCell(Cell comp)
	{
		Location loc = comp.getHeadLocation();
		if (!loc.isValidLocation() || loc.getRow() >= rows || 
				loc.getColumn() >= cols)
			return false;
		
		if (getCellAt(loc) == comp)
		{
			comp.setParentGrid(null);
			pnlBase.getChildren().remove(comp);
			base[loc.getRow()][loc.getColumn()] = null;
			return true;
		}
		return false;
	}

	/**
	 * Updates frame of all the grid components.
	 * @param frame the current frame index.
	 */
	public void updateFrame(long frame)
	{
		for (Node n : pnlBase.getChildren())
		{
			if (n instanceof Cell)
				((Cell)n).updateFrame(frame);
		}
		
		for (Node n : pnlStage.getChildren())
		{
			if (n instanceof Actor)
			{
				Actor a = (Actor)n;
				a.updateFrame(frame);
				if (a.isDead())
					removeActor(a);
			}
		}
	}
	
	/**
	 * Layouts all the children of this Grid.
	 */
	@Override
	protected void layoutChildren()
	{
		double width = computePrefWidth(-1);
		double height = computePrefHeight(-1);
		layoutInArea(pnlBase, 0, 0, width, height, 0, HPos.CENTER, 
				VPos.CENTER);
		layoutInArea(pnlStage, 0, 0, width, height, 0, HPos.CENTER, 
				VPos.CENTER);
		
	}
	
	/**
	 * Checks whether if the actor "visitor" can visit this 
	 * location. It first checks for bounds issues, then
	 * checks if the cell will permit, then whether if the 
	 * actors will permit. If all is well, it will move the
	 * actor.
	 * 
	 * @param visitor the actor visitor that will move.
	 * @param row row of the new location.
	 * @param col column of the new location.
	 * @return true if the visit is permitted, false otherwise.
	 */
	private boolean checkVisit(Actor visitor, int row, int col)
	{
		if (visitor.isMoving())
			return false;
		
		if (!isValidLocation(row, col))
			return false;
		
		if (base[row][col] != null && !base[row][col].pass(visitor))
			return false;
		
		List<Actor> residents = getResidents(visitor, row, col);
		for (Actor res : residents)
		{
			boolean result;
			if (visitor.dominanceLevelFor(res).compareTo(
					res.dominanceLevelFor(visitor)) >= 0)
				result = visitor.interact(res);
			else
				result = res.interact(visitor);
			if (!result)
				return false;
		}
		return true;
	}
	
	/**
	 * Determines whether if a row/cell location is a valid location, i.e.
	 * is a location within this grid's bounds.
	 * @param row the row of the location.
	 * @param col the column of the location.
	 * @return true if valid, false if invalid.
	 */
	private boolean isValidLocation(int row, int col)
	{
		return row >= 0 && col >= 0 && row < cellSize && col < cellSize;
	}
}