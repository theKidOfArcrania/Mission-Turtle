/**
 * Cell.java
 * 
 * Represents a immutable (mostly), immovable grid component, that falls
 * on the bottom layer of the grid.
 * 
 * @author Henry Wang
 * Date: 4/26/17
 * Period: 2
 */

package turtle.core;

public abstract class Cell extends Component
{
	/**
	 * Executes a pass when an visitor actor comes to this cell.
	 * @param visitor the actor passing this cell
	 * @return true if cell allows visitor to pass cell
	 *         false if cell prohibits such a move.
	 */
	public abstract boolean pass(Actor visitor);
}
