package turtle.comp;

import java.util.Map;
import java.util.function.Predicate;

import turtle.core.Actor;
import turtle.core.DominanceLevel;

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
public class Door extends Actor
{
	/** The default image for this component */
	public static final int DEFAULT_IMAGE = 0;
	private static final int LOCK_OFFSET_IMAGE = 0;
	
	private ColorType color;
	
	/**
	 * Constructs a new door, defaulting to the RED color.
	 */
	public Door()
	{
		setColor(ColorType.RED);
	}
	
	/**
	 * Checks whether an interaction with another actor is possible.
	 * This checks if the actor contains a key of this same color to this door.
	 * 
	 * @param other the other actor to interact with.
	 * @return true if the other actor can pass into location
	 *         false if other actor is prohibited to pass.
	 */
	@Override
	public boolean checkInteract(Actor other)
	{
		if (other instanceof Player)
		{
			for (Item itm : ((Player)other).getPocket())
				if (itm instanceof Key && ((Key)itm).getColor() == getColor())
					return true;
		}
		return false;
	}

	/**
	 * Obtains the dominance level of the actor in relation to another actor.
	 * This will be high on the dominance level since it is a fixture. However
	 * like {@link Mover}, it should actually be located near the top of 
	 * z-order. So for ordering, it returns a low dominance level.
	 * 
	 * @param other other actor to compare with (or null for generally).
	 * @return a dominance level of the actor.
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
	 * @return the color of this door
	 */
	public ColorType getColor()
	{
		return color;
	}


	/**
	 * Executes an interaction with another actor. This will only
	 * allow the pass if an actor has a color-matching key. This 
	 * will subsequently take away that key used to open this door.
	 * 
	 * @param other the other actor to interact with.
	 * @return true if the other actor can pass into location
	 *         false if other actor is prohibited to pass.
	 */
	@Override
	public boolean interact(Actor other)
	{
		if (other instanceof Player)
		{
			/**
			 * Searches for a key that matches this color.
			 */
			Item itm = ((Player)other).useItem(new Predicate<Item>()
			{
				
				/**
				 * Tests whether if this item will be usable to this door.
				 * @param t the item to test.
				 * @return true if it works, false if it doesn't.
				 */
				@Override
				public boolean test(Item t)
				{
					return t instanceof Key && ((Key)t).getColor() == 
							getColor();
				}
			});
			return itm != null;
		}
		return false;
	}

	/**
	 * @param color the new color to set for this door
	 * @throws NullPointerException if the color supplied is null.
	 */
	public void setColor(ColorType color)
	{
		if (color == null)
			throw new NullPointerException();
		setImageFrame(color.getImageFrame(LOCK_OFFSET_IMAGE));
		this.color = color;
	}

	/**
	 * Sets a series of parameters for this door. This
	 * class has one parameter attribute that has functionality:
	 * <table>
	 *   <tr>
	 *     <th>Name</th>
	 *     <th>Type</th>
	 *     <th>Description</th>
	 *   </tr>
	 *   <tr>
	 *     <td><code>color</code></td>
	 *     <td><code>int</code></td>
	 *     <td>This sets the color index (0-based) of this door.</td>
	 *   </tr>
	 * </table>
	 * @param params the parameter object.
	 */
	@Override
	public void setParameters(Map<String, Object> params)
	{
		super.setParameters(params);
		Object val = params.get("color");
		if (val != null && val instanceof Integer)
		{
			ColorType colors[] = ColorType.values();
			int ind = (Integer)val;
			if (ind >= 0 && ind < colors.length)
				setColor(colors[ind]);
		}
	}

}
