/**
 * Hint.java
 * 
 * When a player hovers under this actor, the player will be able to
 * receive a message. More specifically, this hint tile will trigger a 
 * hint flag in the player that will automatically disable after player
 * exits hint object.
 */

package turtle.comp;

import java.util.Map;

import turtle.core.Actor;
import turtle.core.DominanceLevel;

public class Hint extends Actor
{
	private String message;
	private Player playerRead;
	
	public Hint()
	{
		// TODO: imagery
		playerRead = null;
		message = "";
	}

	/**
	 * @return the message this hint will tell.
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Sets this hint to show a new message.
	 * @param message the new message to tell.
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * Sets a series of parameters for this hint tile. This
	 * class has one parameter attribute that has functionality:
	 * <table>
	 *   <tr>
	 *     <th>Name</th>
	 *     <th>Type</th>
	 *     <th>Description</th>
	 *   </tr>
	 *   <tr>
	 *     <td><code>message</code></td>
	 *     <td><code>String</code></td>
	 *     <td>This sets the message that this hint will display</td>
	 *   </tr>
	 * </table>
	 * @param params the parameter object.
	 */
	@Override
	public void setParameters(Map<String, Object> params)
	{
		super.setParameters(params);
		Object val = params.get("message");
		if (val != null && val instanceof String)
			message = (String)val;
	}

	/**
	 * Executes an interaction with another actor. This will always 
	 * allow any actor to pass through. However, if the actor is a
	 * player, we will inform the player of the hint.
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
			Player p = (Player)other;
			playerRead = p;
			p.setMessage(message, this);
		}
		return true;
	}

	/**
	 * Obtains dominance level for actor. This actor will always be
	 * a fixture dominance, having a relatively high dominance level.
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
	 * Updates animation frame of component. This will also check for 
	 * the player location whether if it has left the hint tile.
	 * 
	 * @param frame the animation frame.
	 */
	@Override
	public void updateFrame(long frame)
	{
		super.updateFrame(frame);
		if (playerRead != null && playerRead.getHeadLocation().equals(
				getHeadLocation()))
		{
			playerRead.resetMessage(this);
			playerRead = null;
		}
	}

	
	
}
