package turtle.comp;

import turtle.core.Actor;
import turtle.core.DominanceLevel;
import turtle.core.Grid;
import turtle.core.Location;

/**
 * Mover.java
 * 
 * Represents any object that can be physically moved by a player.
 * @author Henry Wang
 * Period: 2
 * Date: 5/2/17
 */
public abstract class Mover extends Actor
{

	/**
	 * Executes an interaction with another actor. This will move in the
	 * specified vector of the player. In other words, this will execute
	 * the "push" that the player has executed on this actor. Only the
	 * player can move this actor. Other actors, including other movers
	 * may not move each other.
	 * 
	 * @param other the other actor to interact with.
	 * @return true if the other actor can pass into location
	 *         false if other actor is prohibited to pass.
	 */
	@Override
	public boolean interact(Actor other)
	{
		if (!(other instanceof Player))
			return false;
		
		int dir = getPlayerVector();
		if (dir == -1)
			return false;
		return traverseDirection(dir);
	}

	/**
	 * Obtains the dominance level of a mover. Movers should be near the top of
	 * the z-order, therefore in such a case, when calculating z-order,
	 * the mover will return an artificially low dominance level. However,
	 * in other cases, it will have a relatively high dominance level (FIXTURE).
	 * 
	 * @param other the other actor to compare with.
	 * @return dominance level of a mover.
	 */
	@Override
	public DominanceLevel dominanceLevelFor(Actor other)
	{
		if (other == null)
			return FLOATING;
		else
			return FIXTURE;
	}
	
	/**
	 * Obtains the vector the player is pushing against this mover at, based 
	 * on the player's relative position to this mover.
	 * 
	 * @return directionals 0 to 3, or -1 if there is no vector.
	 */
	protected int getPlayerVector()
	{
		Grid parent = getParentGrid();
		if (parent == null)
			return -1;
		
		final int poss[] = {-1, 0, 0, 1, 1, 0, 0, -1};
		
		Location playerLoc = parent.getPlayer().getHeadLocation();
		Location blockLoc = getHeadLocation();
		
		int dr = blockLoc.getRow() - playerLoc.getRow();
		int dc = blockLoc.getColumn() - playerLoc.getColumn();
		
		int dir;
		for (dir = 0; dir < poss.length; dir += 2)
		{
			if (poss[dir] == dr && poss[dir + 1] == dc)
				break;
		}
		
		if (dir >= poss.length)
			return -1;
		else
			return dir / 2;
	}
}
