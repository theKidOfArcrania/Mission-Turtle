package turtle.comp;

import turtle.core.Actor;
import turtle.core.Component;
import turtle.core.DominanceLevel;

/**
 * Trap.java
 * 
 * This actor will kill any victim that happens to step over this contraption.
 * The trap will then immediately disintegrate. 
 * 
 * @author Henry
 * Date: 5/7/17
 * Period: 2
 */
public class Trap extends Actor
{
	/** The default image for this component */
	public static final int DEFAULT_IMAGE = 56;
	private static final int TRAP_STILL_IMAGE = 56;
	private static final int[] ANIMATION_IMAGE = {57, 58, 59};
	
	/**
	 * Constructs a new trap actor and initializes image.
	 */
	public Trap()
	{
		setImageFrame(TRAP_STILL_IMAGE);
	}

	/**
	 * Checks if an actor can pass through this trap.
	 * 
	 * @param other the actor to pass through
	 * @return true always to allow passing into location
	 */
	@Override
	public boolean checkInteract(Actor other)
	{
		return true;
	}

	/**
	 * Interacts with other actors. This will kill everything
	 * on contact. If the actor dies, this will also self-destruct itself.
	 * 
	 * @param other the other actor to interact with.
	 * @return true always to allow passing into location
	 */
	@Override
	public boolean interact(Actor other)
	{
		if (other.die(this))
			die(this);
		return true; 
	}

	/**
	 * Obtains dominance level for actor. This actor has a fixture
	 * dominance level since it passively destroys many. 
	 * 
	 * @param other the other actor to compare with.
	 * @return dominance level.
	 */
	@Override
	public DominanceLevel dominanceLevelFor(Actor other)
	{
		return FIXTURE;
	}

	/**
	 * Handles dying process. This will only die if it is triggered
	 * by someone else. In that event, it will effectively kill itself.
	 * 
	 * @param attacker the component that initiated the attack.
	 * @return true if it dies, false otherwise.
	 */
	@Override
	public boolean die(Component attacker)
	{
		if (attacker == this && super.die(attacker))
		{
			animateFrames(ANIMATION_IMAGE, false);
			return true;
		}
		return false;
	}
	
	/**
	 * This overrides the Actor's setHeading since a heading does not
	 * mean anything for this actor.
	 */
	@Override
	public void setHeading(int heading)
	{
		//Does nothing
	}
}
