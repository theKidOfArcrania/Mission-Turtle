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
	public static final int DEFAULT_IMAGE = 17;
	private static final int EXIT_IMAGE = 17;
	
	private Player winner;
	
	public Exit()
	{
		setImageFrame(EXIT_IMAGE);
	}
	
	@Override
	public boolean pass(Actor visitor)
	{
		if (visitor instanceof Player)
		{
			if (getParentGrid().getFoodRequirement() <= 0)
				winner = (Player)visitor;
			return true;
		}
		return false;
	}

	/**
	 * Updates each animation frame so that it will set winner flag
	 * when player has finished moving into exit.
	 * @param frame the frame number.
	 */
	@Override
	public void updateFrame(long frame)
	{
		super.updateFrame(frame);
		if (winner != null && winner.getTrailingLocation().equals(
				getHeadLocation()))
			winner.win();
	}
}
