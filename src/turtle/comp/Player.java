/**
 * Represents the player unit within the game. The player would directly
 * control this character within the game.
 * 
 * @author Henry Wang
 * Date: 4/28/17
 * Period: 2
 */

package turtle.comp;

import java.util.ArrayList;
import java.util.function.Predicate;

import turtle.core.Actor;
import turtle.core.Component;
import turtle.core.DominanceLevel;

public class Player extends Actor
{
	public static final int DEFAULT_IMAGE = 40;
	private static final int FRAME_STILL = 40;
	private static final int[] FRAME_ANIMATE = {41,42,43,40};
	
	private Component msgSender;
	private String msg;
	private boolean winner;
	private boolean moving;
	
	private final ArrayList<Item> pocket;
	
	/**
	 * Constructs a new player.
	 */
	public Player()
	{
		winner = false;
		moving = false;
		setImageFrame(FRAME_STILL);
		pocket = new ArrayList<>();
	}
	
	/**
	 * Adds a new item to the player. This will currently only accept 
	 * keys. 
	 * @param itm the item to add.
	 * @return true if this item is collected, false if it is not.
	 */
	public boolean collectItem(Item itm)
	{
		if (itm instanceof Key)
		{
			pocket.add(itm);
			return true;
		}
		return false;
	}
	
	/**
	 * Finds an item and removes the first such match for the player to use.
	 * @param usable a function used to identify which item is usable.
	 * @return the first item, or null if it cannot be found.
	 */
	public Item useItem(Predicate<Item> usable)
	{
		Item found = null;
		for (Item itm : pocket)
			if (usable.test(itm))
			{
				found = itm;
				break;
			}
		
		if (found != null)
			pocket.remove(found);
		return found;
	}
	
	/**
	 * Kills this actor (this sets a flag for this actor to be removed).
	 * This overrides it to be immune to water. 
	 * 
	 * @param attacker the thing that is killing this actor.
	 * @return true if this actor died as a result of this call, false if
	 * 		nothing changed.
	 */
	@Override
	public boolean die(Component attacker)
	{
		if (attacker instanceof Water)
			return false;
		return super.die(attacker);
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
	 * @return the message that the player should see now. Never returns null.
	 * @see #setMessage(String, Component)
	 */
	public String getMessage()
	{
		if (msg == null)
			return "";
		return msg;
	}
	
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
	 * Determines whether if player won the game.
	 * @return true if player won, false if game is still running.
	 */
	public boolean isWinner()
	{
		return winner;
	}

	/**
	 * Resets message if the sender sent this message (i.e. it has not
	 * already been overriden by someone else.
	 * 
	 * @param sender the component that sent the message
	 */
	public void resetMessage(Component sender)
	{
		if (msgSender == sender)
			this.msg = null;
	}
	
	/**
	 * This is set as a flag so that the UI will then be able
	 * to go and display this message to the player. This mechanism
	 * will allow the user to read some information that might
	 * help them along in the level. 
	 * 
	 * @param msg the new message to show the player.
	 * @param sender the component sending the message.
	 */
	public void setMessage(String msg, Component sender)
	{
		this.msg = msg;
		this.msgSender = sender;
	}
	
	/**
	 * Updates frame of component. This changes the turtle animations from moving
	 * and not moving whenever it is moving or not.
	 * @param frame frame number
	 */
	@Override
	public void updateFrame(long frame)
	{
		super.updateFrame(frame);
		if (moving ^ isMoving())
		{
			moving = !moving;
			if (moving)
				animateFrames(FRAME_ANIMATE, true);
			else
				setImageFrame(FRAME_STILL);
		}
		
	}

	/**
	 * Flags that the player has won the game.
	 */
	public void win()
	{
		winner = true;
	}
}
