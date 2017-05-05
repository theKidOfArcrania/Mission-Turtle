/**
 * Exit.java
 * 
 * This is the spot
 */

package turtle.comp;

import turtle.core.Actor;
import turtle.core.Cell;

public class Exit extends Cell
{

	@Override
	public boolean pass(Actor visitor)
	{
		if (visitor instanceof Player)
		{
			if (getParentGrid().getFoodRequirement() <= 0)
				((Player)visitor).win();
			return true;
		}
		return false;
	}

}
