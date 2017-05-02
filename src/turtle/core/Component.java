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
	public static final int DEF_ANIMATION_FRAME_CHANGE = 4;
	
	public static final double MOVE_SPEED = 10.0;
	
	public static final TileSet DEFAULT_SET = new TileSet();
	
	private final TileSet ts;
	private final ImageView img;
	private final Location headLoc;
	private final Location trailLoc;
	private Grid parent;
	
	private long curFrame;
	
	private long animationOffset;
	private int[] imageFrames;
	private int changeRate;
	private boolean animationCycle;
	
	/**
	 * Constructs a new component with the image background.
	 */
	protected Component()
	{
		ts = DEFAULT_SET;
		
		img = new ImageView();
		this.getChildren().add(img);
		
		headLoc = new Location();
		trailLoc = new Location();
		
		img.setImage(ts.getTileset());
		img.setViewport(new Rectangle2D(0, 0, 0, 0));
		
		curFrame = 0;
		animationOffset = -1;
		imageFrames = null;
		changeRate = -1;
	}

	/**
	 * Animates component through a series of frames at the default
	 * change rate (change once per 4 frames).
	 * @param imageFrames the animation frames to animate through.
	 * @param animationCycle true to cycle through frames, false to run once. 
	 * @throws IndexOutOfBoundsException if any of the frame indexes are 
	 * 		out of bounds.
	 * @throws IllegalArgumentException if the changeRate is not positive.
	 * @see #animateFrames(int[], boolean, int)
	 */
	public void animateFrames(int[] imageFrames, boolean animationCycle)
	{
		animateFrames(imageFrames, animationCycle, DEF_ANIMATION_FRAME_CHANGE);
	}
	
	/**
	 * Sets this component to animate through the frames indexes at a
	 * specified change rate.
	 * 
	 * @param imageFrames the image frames to animate through.
	 * @param animationCycle true to cycle through frames, false to run once. 
	 * @param changeRate the number of frames to wait between frames.
	 * @throws IndexOutOfBoundsException if any of the frame indexes are 
	 * 		out of bounds.
	 * @throws IllegalArgumentException if the changeRate is not positive.
	 */
	public void animateFrames(int[] imageFrames, boolean animationCycle, 
			int changeRate)
	{
		//Test for index out of bounds.
		for (int f : imageFrames)
			ts.frameAt(f); 
		
		if (changeRate <= 0)
			throw new IllegalArgumentException("changeRate must be positive.");
		
		animationOffset = curFrame;
		this.animationCycle = animationCycle;
		this.imageFrames = imageFrames;
		this.changeRate = changeRate;
		
		img.setViewport(ts.frameAt(imageFrames[0]));
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
	 * Sets the image of this component to the given index and disables
	 * any current animations.
	 * @param index the index from the TileSet of frames.
	 * @throws IndexOutOfBoundsException if image frame index is out of bounds.
	 */
	public void setImageFrame(int index)
	{
		img.setViewport(ts.frameAt(index));
		animationOffset = -1;
		imageFrames = null;
		changeRate = -1;
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
	 * @param frame current frame number
	 */
	public void updateFrame(long frame)
	{
		curFrame = frame;
		updateAnimation(frame);
		move();
	}

	/**
	 * Updates the current image to step forward one frame if it is in a sequence
	 * of animation.
	 * @param frame the current frame number.
	 */
	private void updateAnimation(long frame)
	{
		if (animationOffset != -1 && (frame - animationOffset) % changeRate == 0)
		{
			int stepInd = (int)((frame - animationOffset) / changeRate);
			if (animationCycle)
			{
				stepInd %= imageFrames.length;
				img.setViewport(ts.frameAt(imageFrames[stepInd]));
			}
			else
			{
				if (stepInd >= imageFrames.length - 1)
					setImageFrame(imageFrames[imageFrames.length - 1]);
				else
					img.setViewport(ts.frameAt(imageFrames[stepInd]));
			}
		}
	}

	/**
	 * Moves this component one frame step in the direction it is moving if any.
	 */
	private void move()
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
	 * @throws IllegalArgumentException if step is zero.
	 */
	private double increment(double from, double to, double step)
	{
		if (step == 0)
			throw new IllegalArgumentException("Step must be non-zero.");
		if (from == to)
			return to;
		if (from < to ^ step > 0)
			return increment(from, to, -step);
			
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
