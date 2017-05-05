/**
 * Item.java
 * 
 * Represents a group of stuff that a player can collect.
 * @author Henry Wang
 * Period: 2
 * Date: 5/3/17
 */

package turtle.comp;

import turtle.core.Actor;
import turtle.core.DominanceLevel;

public abstract class Item extends Actor
{

	/**
	 * Interacts with other actors, allowing the player to pick up this item.
	 * @param other other actor to interact with.
	 * @return always returns true to allow anything to pass through it.
	 */
	@Override
	public boolean interact(Actor other)
	{
		if (other instanceof Player)
		{
			Player p = (Player)other;
			p.collectItem(this);
		}
		return true;
	}

	/**
	 * Obtains the dominance level of the actor in relation to another actor.
	 * This will always return ITEM, so it is pretty on the dominance level
	 * list.
	 * 
	 * @param other other actor to compare with (or null for generally).
	 * @return a dominance level of the actor.
	 */
	@Override
	public DominanceLevel dominanceLevelFor(Actor other)
	{
		return ITEM;
	}

	/**
	 * Checks whether if this item is identical as another.
	 * @param other other item to compare with
	 * @return true if both items are identical
	 */
	public abstract boolean identical(Item other);

	/**
	 * Obtains the index of the image to display as an item.
	 * @return the index of image as item.
	 */
	public abstract int getItemImage();
}
