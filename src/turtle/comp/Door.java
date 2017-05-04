/**
 * Door.java
 * 
 * This acts as a wall until an actor passes through this door, which contains a
 * color-matching key. At this point the actor will remove itself.
 * 
 * @author Henry Wang
 * Period: 2
 * Date: 5/4/17
 */

package turtle.comp;

import turtle.core.Actor;
import turtle.core.DominanceLevel;

public class Door extends Actor
{
	public static final int DEFAULT_IMAGE = 0;
	
	@Override
	public boolean interact(Actor other)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DominanceLevel dominanceLevelFor(Actor other)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
