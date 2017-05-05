/**
 * Fire.java
 * 
 * This kills all actor visitors by the force of fire!
 * 
 * @author Henry Wang
 * Period: 2
 * Date: 5/4/17
 */

package turtle.comp;

import turtle.core.Actor;
import turtle.core.Cell;

public class Fire extends Cell
{
	public static final int DEFAULT_IMAGE = 18; 
	private static final int[] ANIMATE_FRAMES = {18, 19, 20};
	private static final int[] TRANSFORM_FRAMES = {19, 21, 22, 23, 24, 25, 26};
	
	private static final int SHUFFLE = 50;
	
	/**
	 * Constructs a new fire cell by initializing UI.
	 */
	public Fire()
	{
		int[] randomized = ANIMATE_FRAMES.clone();
		for (int i = 0; i < SHUFFLE; i++)
		{
			int a = (int)(Math.random() * randomized.length);
			int b = (int)(Math.random() * randomized.length);
			int tmp = randomized[a];
			randomized[a] = randomized[b];
			randomized[b] = tmp;
		}
		animateFrames(randomized, true);
	}
	
	/**
	 * Kills all actors that visit this fire
	 * @param visitor the actor that comes over this cell.
	 * @return true always, since they are permitted to come here.
	 */
	@Override
	public boolean pass(Actor visitor)
	{
		visitor.die(this);
		return true;
	}

	/**
	 * Transforms this fire into just sand.
	 */
	public void transformToSand()
	{
		animateFrames(TRANSFORM_FRAMES, false);
		transformTo(new Sand(), DEF_ANIMATION_FRAME_CHANGE * TRANSFORM_FRAMES.length);
	}
}
