/**
 * Cell.java
 * 
 * Represents a immutable (mostly), immovable grid component, that falls
 * on the bottom layer of the grid.
 * 
 * @author Henry Wang
 * Date: 4/26/17
 * Period: 2
 */

package turtle.core;

public abstract class Cell extends Component
{
	private int frameTransform;
	private Cell transformed;
	
	/**
	 * Constructs a new cell.
	 */
	public Cell()
	{
		frameTransform = -1;
		transformed = null;
	}
	
	/**
	 * Executes a pass when an visitor actor comes to this cell.
	 * @param visitor the actor passing this cell
	 * @return true if cell allows visitor to pass cell
	 *         false if cell prohibits such a move.
	 */
	public abstract boolean pass(Actor visitor);
	
	/**
	 * Utility method used to transform this cell into something else.
	 * Of course, it doesn't literally change this cell, but it just
	 * creates a new cell that fill the place of this cell's location.
	 * 
	 * @param other the other cell to transform into.
	 * @param waitFrames the number of frames to wait for transformation.
	 * @throws IllegalStateException if this cell isn't added to anything.
	 * @throws IllegalArgumentException if cell to transform to is alraedy added to grid.
	 */
	public void transformTo(Cell other, int waitFrames)
	{
		if (transformed != null)
			return;
		
		Grid parent = getParentGrid();
		if (parent == null)
			throw new IllegalStateException("Parent grid is non-existent!");
		if (other.getParentGrid() != null)
			throw new IllegalArgumentException("Cell transformed already added!");
		
		frameTransform = waitFrames;
		transformed = other;
	}
	
	/**
	 * Updates a frame of animation for a cell. This updates the transform animations.
	 * @param frame current frame number
	 */
	@Override
	public void updateFrame(long frame)
	{
		super.updateFrame(frame);
		if (transformed != null && frameTransform >= 0)
		{
			frameTransform--;
			if (frameTransform == -1)
			{
				Grid parent = getParentGrid();
				if (parent != null)
				{
					
					transformed.getHeadLocation().setLocation(getHeadLocation());
					parent.removeCell(this);
					parent.placeCell(transformed);
				}
			}
		}
	}
}
