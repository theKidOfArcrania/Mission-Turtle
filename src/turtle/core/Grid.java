package turtle.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import turtle.comp.Player;

/**
 * Grid.java
 * 
 * Manages and displays all the grid components in the level.
 * @author Henry Wang
 * Date: 4/27/17
 * Period: 2
 */
public class Grid extends Pane
{
	
	
	private static final Color SHADOW_COLOR = Color.web("#101010");
	private static final double SHADOW_RADIUS = 20;
	
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
	
	private final int rows;
	private final int cols;
	private final int cellSize;
	
	private final Cell[][] base;
	private final HashMap<Actor, Location> actorLocs;
	
	private Pane pnlBase;
	private Pane pnlStage;
	
	private Player player;
	
	private int foodLeft;
	
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
		foodLeft = 0;
		
		base = new Cell[rows][cols];
		actorLocs = new HashMap<>();
		
		pnlBase = new ComponentPane();
		pnlStage = new ComponentPane();
		
		setEffect(new DropShadow(SHADOW_RADIUS, SHADOW_COLOR));
		
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
		residents.sort(new Comparator<Actor>() 
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
	 * @return the amount of food requirements left.
	 */
	public int getFoodRequirement()
	{
		return foodLeft; 
	}
	
	/**
	 * @param foodLeft new amount of food to collect
	 */
	public void setFoodRequirement(int foodLeft)
	{
		this.foodLeft = foodLeft;
	}
	
	/**
	 * Increment the amount of food by one unit, and thus the amount of
	 * food requirement is decremented.
	 */
	public void incrementFood()
	{
		if (foodLeft > 0)
			foodLeft--;
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
		if (comp.getParentGrid() != null)
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
			
			int insertInd;
			for (insertInd = 0; insertInd < children.size(); insertInd++)
			{
				if (children.get(insertInd) instanceof Actor)
				{
					Actor child = (Actor) children.get(insertInd);
					if (child.dominanceLevelFor(null).compareTo(test) > 0)
						break;
				}
			}
			children.add(insertInd, comp);
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
		if (comp.getParentGrid() != null)
			return false;
		if (pnlBase.getChildren().contains(comp))
			return false;
		
		Location loc = comp.getHeadLocation();
		if (base[loc.getRow()][loc.getColumn()] != null)
			return false;
		
		comp.setParentGrid(this);
		comp.getTrailingLocation().setLocation(loc);
		comp.setTranslateX(loc.getColumn() * cellSize);
		comp.setTranslateY(loc.getRow() * cellSize);
		
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
		//Avoid concurrency issues.
		List<Node> base = new ArrayList<>(pnlBase.getChildren());
		List<Node> stage = new ArrayList<>(pnlStage.getChildren());
		
		for (Node n : base)
		{
			if (n instanceof Cell)
				((Cell)n).updateFrame(frame);
		}
		
		for (Node n : stage)
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
		double width = cellSize * cols;
		double height = cellSize * rows;
		
		layoutInArea(pnlBase, 0, 0, width, height, 0, HPos.CENTER, 
				VPos.CENTER);
		layoutInArea(pnlStage, 0, 0, width, height, 0, HPos.CENTER, 
				VPos.CENTER);
	}
	
	/**
	 * Checks whether if the actor "visitor" can visit this 
	 * location. It first checks for bounds issues. Then
	 * it makes a preliminary check, then executes the 
	 * visiting action. It always starts with the cell,
	 * then moves up in actor dominance from highest to lowest
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
		
		if (base[row][col] != null && !base[row][col].checkPass(visitor))
			return false;
		
		List<Actor> residents = getResidents(visitor, row, col);
		residents.remove(visitor);
		
		Actor[] master = new Actor[residents.size()];
		Actor[] slave = new Actor[residents.size()];
		for (int i = 0; i < residents.size(); i++)
		{
			Actor res = residents.get(i);
			if (visitor.dominanceLevelFor(res).compareTo(
					res.dominanceLevelFor(visitor)) >= 0)
			{
				master[i] = visitor;
				slave[i] = res;
			}
			else
			{
				master[i] = res;
				slave[i] = visitor;
			}
			if (!master[i].checkInteract(slave[i]))
				return false;
		}
		
		if (base[row][col] != null && !base[row][col].pass(visitor))
			return false;
		
		for (int i = 0; i < residents.size(); i++)
			if (!master[i].interact(slave[i]))
				return false;
		
		visitor.getHeadLocation().setLocation(row, col);
		
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
		return row >= 0 && col >= 0 && row < rows && col < cols;
	}
}
