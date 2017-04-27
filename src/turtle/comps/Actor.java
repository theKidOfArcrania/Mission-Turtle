/**
 * Represents a movable/ interactable grid component that is readily mutable
 * 
 * @author Henry Wang
 * Date: 4/26/17
 * Period: 2 
 */

package turtle.comps;

public abstract class Actor extends Component
{
	//Directions
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	
	//Some common dominance levels.
	public static final DominanceLevel PLAYER = new DominanceLevel("Player", 0);
	public static final DominanceLevel ENEMY = new DominanceLevel("Enemy", 100);
	public static final DominanceLevel ITEM = new DominanceLevel("Item", 200);
	public static final DominanceLevel FIXTURE = new DominanceLevel("Fixture", 300);
	
	/**
	 * Executes an interaction with another actor.
	 * 
	 * @param other the other actor to interact with.
	 * @return true if the other actor can pass into location
	 *         false if other actor is prohibited to pass.
	 */
	public abstract boolean interact(Actor other);
	
	/**
	 * Obtains the dominance level of the actor in relation to another actor.
	 * @param other other actor to compare with (or null for generally).
	 * @return a dominance level of the actor.
	 */
	public abstract DominanceLevel dominanceLevelFor(Actor other);
	
	/**
	 * Moves this actor one space in specified direction. 
	 * @param direction direction to move in.
	 * @return true if successful, false otherwise.
	 */
	public boolean traverseDirection(int direction)
	{
		Grid parent = getParentGrid();
		if (parent == null)
			return false;
		
		Location loc = getHeadLocation();
		int row = loc.getRow();
		int col = loc.getColumn();
		
		switch (direction)
		{
		case NORTH: row--; break;
		case EAST: col++; break;
		case SOUTH: row++; break;
		case WEST: col--; break;
		default: return false;
		}
		
		return parent.moveActor(this, row, col);
	}
}
