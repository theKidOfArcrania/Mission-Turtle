/**
 * Wall.java
 * This represents a static non-moving obstacle that denies access
 * to any actor that might try to pass through this area.
 * 
 * @author Henry Wang
 * Period: 2
 * Date: 5/3/17
 */

package turtle.comp;

import turtle.core.Actor;
import turtle.core.Cell;

public class Wall extends Cell
{
	private static final int WALL_IMAGE = 51;
	
	/**
	 * Constructs a new Wall by initializing the image.
	 */
	public Wall()
	{
		setImageFrame(WALL_IMAGE);
	}
	
	/**
	 * Executes a pass when an visitor actor comes to this cell. This
	 * lets no one to pass this wall. It will block everything.
	 * 
	 * @param visitor the actor passing this cell
	 * @return false always to prohibit such a move.
	 */
	@Override
	public boolean pass(Actor visitor)
	{
		return false;
	}

}
