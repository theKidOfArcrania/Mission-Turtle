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
	 * Obtains the dominance level of the actor.
	 * @return a dominance level of the actor.
	 */
	public abstract DominanceLevel getDominanceLevel();
	
}
