package turtle.comp;

import turtle.core.Actor;
import turtle.core.Cell;

/**
 * Sand.java
 * 
 * Represents the basic land entity.
 * 
 * @author Henry Wang
 * Date: 5/5/17
 * Period: 2
 */
public class Sand extends Cell
{

	public static final int DEFAULT_IMAGE = 26;
	private static final int SAND_IMAGE = 26;
	
	/**
	 * Constructs a new sand and initializing UI.
	 */
	public Sand()
	{
		setImageFrame(SAND_IMAGE);
	}
	
	/**
	 * Executes a pass when an visitor actor comes to this cell. This
	 * lets everything to pass this sand.
	 * 
	 * @param visitor the actor passing this cell
	 * @return true always to permit such a move.
	 */
	@Override
	public boolean pass(Actor visitor)
	{
		return true;
	}

}
