/**
 * Represents the player unit within the game. The player would directly
 * control this character within the game.
 * 
 * @author Henry Wang
 * Date: 4/28/17
 * Period: 2
 */

package turtle.comp;

import turtle.core.Actor;
import turtle.core.DominanceLevel;

public class Player extends Actor
{

	private String msg;
	
	/**
	 * Interacts with other actors. This does nothing since every actor
	 * should dominate over player.
	 * @param other other actor to compare with.
	 * @return always true.
	 */
	@Override
	public boolean interact(Actor other)
	{
		return true;
	}

	/**
	 * Obtains the dominance level of this player. It will always
	 * have the lowest dominance level of all characters.
	 * @param other the other actor to compare with.
	 * @return dominance level of player.
	 */
	@Override
	public DominanceLevel dominanceLevelFor(Actor other)
	{
		return PLAYER;
	}

	/**
	 * @return the message that the player should see now.
	 * @see #setMessage(String)
	 */
	public String getMessage()
	{
		return msg;
	}

	/**
	 * This is set as a flag so that the UI will then be able
	 * to go and display this message to the player. This mechanism
	 * will allow the user to read some information that might
	 * help them along in the level. 
	 * 
	 * @param msg the new message to show the player
	 */
	public void setMessage(String msg)
	{
		this.msg = msg;
	}
	
	
}
