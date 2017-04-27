/**
 * Component.java
 * 
 * Represents the abstract base of all grid components
 * that will be displayed on the Grid.
 * 
 * @author Henry Wang
 * Date: 4/26/17
 * Period: 2
 */
package turtle.core;

import java.util.Map;

import javafx.geometry.HPos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public abstract class Component extends Pane 
{
	public static final double MOVE_SPEED = 10.0;
	
	public static final TileSet DEFAULT_SET = new TileSet();
	
	private final ImageView img;
	private final Location headLoc;
	private final Location trailLoc;
	private Grid parent;
	
	/**
	 * Constructs a new component with the image background.
	 */
	protected Component()
	{
		img = new ImageView();
		this.getChildren().add(img);
		
		headLoc = new Location();
		trailLoc = new Location();
		
		img.setImage(DEFAULT_SET.getTileset());
		img.setViewport(new Rectangle2D(0, 0, 0, 0));
	}

	/**
	 * Changes any parameters (if any) associated with this component.
	 * Any subclasses overriding this method is STRONLY advised to 
	 * call <code>super.setParameters</code> (or any of such derivative)
	 * in order that the parameters are set properly.
	 * @param params parameters in a map to dynamically set attributes. 
	 */
	public void setParameters(Map<String, Object> params)
	{
		//Does nothing
	}
	
	/**
	 * @return the head location of the actor
	 */
	public Location getHeadLocation()
	{
		return headLoc;
	}

	/**
	 * Obtains the move speed of this component.
	 * @return move speed in pixels per frame
	 */
	public double getMoveSpeed()
	{
		return MOVE_SPEED;
	}

	/**
	 * @return the parent grid that contains this component
	 * 	    or null if there is no parent.
	 */
	public Grid getParentGrid()
	{
		return parent;
	}

	/**
	 * @return the trailing location of the actor 
	 */
	public Location getTrailingLocation()
	{
		return trailLoc;
	}

	/**
	 * Determines whether if this component is moving.
	 * @return true if moving, false if it is still
	 */
	public boolean isMoving()
	{
		return !headLoc.equals(trailLoc);
	}
	
	/**
	 * Sets the image of this component to the given index.
	 * @param index the index from the TileSet of frames.
	 */
	public void setImageFrame(int index)
	{
		img.setViewport(DEFAULT_SET.frameAt(index));
	}
	
	/**
	 * Sets a new parent grid. This should only be called internally
	 * by Grid when this is added.
	 * @param parent the new parent grid to set to
	 */
	protected void setParentGrid(Grid parent)
	{
		this.parent = parent;
	}
	
	/**
	 * Updates a frame of animation for a Component.
	 * Note: Subclasses should ALWAYS call <code>super.updateFrame()</code>
	 * in order that all the super classes get updated as well.
	 * @return current frame number
	 */
	public void updateFrame(long frame)
	{
		if (parent != null)
		{
			boolean validLocs = headLoc.isValidLocation() && 
					trailLoc.isValidLocation();
			if (validLocs)
			{
				double speed = getMoveSpeed();
				int cellSize = parent.getCellSize();
				int xPos = cellSize * headLoc.getColumn();
				int yPos = cellSize * headLoc.getRow();
				if (xPos != getTranslateX())
					setTranslateX(increment(getTranslateX(), xPos, speed));
				if (yPos != getTranslateY())
					setTranslateY(increment(getTranslateY(), yPos, speed));
				
				if (xPos == getTranslateX() && yPos == getTranslateY())
					trailLoc.setLocation(headLoc);
			}
		}
	}
	
	/**
	 * Obtains next step of incrementing a value
	 * @param from intial value
	 * @param to the goal value to achieve
	 * @param step the increment value per step
	 * @return the next value to increment to.
	 */
	private double increment(double from, double to, double step)
	{
		if (from == to)
			return to;
		
		double after = from + step;
		if (after > to ^ from > to)
			// Incremented pass the target.
			return to; 
		else
			return after;
	}
	
	/**
	 * Layouts the children (i.e. ImageView)
	 * of this Component object
	 */
	@Override
	protected void layoutChildren()
	{
		layoutInArea(img, 0, 0, getWidth(), getHeight(), 0, HPos.CENTER, 
				VPos.CENTER);
	}
}
