package turtle.comp;

import java.util.Map;

import turtle.core.Component;

/**
 * Bucket.java
 * This actor can interacts in the game by filling up with water in water cells
 * and quells fire cells.
 * 
 * @author Henry
 * Date: 5/9/17
 * Period: 2
 */
public class Bucket extends Mover
{
	/** The default image for this component */
	public static final int DEFAULT_IMAGE = 31;
	
	private static final int EMPTY_IMAGE = 31;
	private static final int[] ANIMATE_FRAMES = {32,33,34};
	
	private boolean filled;
	
	/**
	 * Constructs a Bucket and initializes image.
	 */
	public Bucket()
	{
		setImageFrame(EMPTY_IMAGE);
	}
	
	/**
	 * Overrides die procedure, so that bucket will fill on contact of water
	 * and it will quell fire if it is filled.
	 * 
	 * @param attacker the component attacking bucket.
	 * @return true if this died as a result of call.
	 */
	@Override
	public boolean die(Component attacker)
	{
		if (attacker instanceof Water)
		{
			if (!isFilled())
			{
				setFilled(true);
				((Water)attacker).transformToSand();
			}
			return false;
		}
		else if (attacker instanceof Fire)
		{
			if (isFilled())
				((Fire)attacker).transformToSand();
		}
		return super.die(attacker);
	}
	

	/**
	 * Sets a series of parameters for this bucket actor. This
	 * class has one parameter attribute that has functionality:
	 * <table>
	 *   <tr>
	 *     <th>Name</th>
	 *     <th>Type</th>
	 *     <th>Description</th>
	 *   </tr>
	 *   <tr>
	 *     <td><code>filled</code></td>
	 *     <td><code>boolean</code></td>
	 *     <td>This sets the state whether if the bucket is filled or empty</td>
	 *   </tr>
	 * </table>
	 * @param params the parameter object.
	 */
	@Override
	public void setParameters(Map<String, Object> params)
	{
		super.setParameters(params);
		Object val = params.get("filled");
		if (val != null && val instanceof Boolean)
			setFilled((Boolean)val);
	}

	
	/**
	 * @return whether if this bucket is filled with water.
	 */
	public boolean isFilled()
	{
		return filled;
	}

	/**
	 * Sets whether if bucket is filled and edits image.
	 * @param filled true to be filled with water, false if empty. 
	 */
	public void setFilled(boolean filled)
	{
		this.filled = filled;
		if (filled)
		{
			int[] randomized = ANIMATE_FRAMES.clone();
			shuffle(randomized);
			animateFrames(randomized, true);
		}
		else
			setImageFrame(EMPTY_IMAGE);
	}
	
	
}
