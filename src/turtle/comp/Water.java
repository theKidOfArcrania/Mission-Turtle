package turtle.comp;

import turtle.core.Actor;
import turtle.core.Cell;

public class Water extends Cell
{
	public static final int DEFAULT_IMAGE = 12;
	
	private static final int[] ANIMATION_FRAME = {12,13,14,15,16};
	/**
	 * Constructs a Water tile and initializes UI.
	 */
	public Water()
	{
		animateFrames(ANIMATION_FRAME, true);
	}
	
	/**
	 * Kills everything that passes it.
	 * @param visitor the actor passing this cell.
	 * @return always returns true to allow visitor to pass cell
	 */
	@Override
	public boolean pass(Actor visitor)
	{
		visitor.die(this);
		return true;
	}

}
