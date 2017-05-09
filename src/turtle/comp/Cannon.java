package turtle.comp;

import java.util.Map;

import turtle.core.Actor;
import turtle.core.Grid;
import turtle.core.Location;

public class Cannon extends Mover
{
	/** The default image for this component */
	public static final int DEFAULT_IMAGE = 35;
	
	private static final int DEFAULT_SHOOTING_PERIOD = 2;
	
	private static final int STILL_IMAGE = 35;
	private static final int[] SHOOTING_ANIMATION = {36,37,38,35};
	
	private int period;
	
	/**
	 * Constructs a new cannon and sets up image.
	 */
	public Cannon()
	{
		setImageFrame(STILL_IMAGE);
		period = DEFAULT_SHOOTING_PERIOD; 
	}
	
	private void shoot()
	{
		Grid parent = getParentGrid();
		if (parent == null)
			return;
		
		Location loc = getHeadLocation();
		int row = loc.getRow();
		int col = loc.getColumn();

		switch (getHeading())
		{
		case NORTH: row--; break;
		case EAST: col++; break;
		case SOUTH: row++; break;
		case WEST: col--; break;
		default: return;
		}
		
		animateFrames(SHOOTING_ANIMATION, false);
		
		Projectile p = new Projectile();
		p.setHeading(getHeading());
		p.getHeadLocation().setLocation(row, col);
		p.getTrailingLocation().setLocation(row, col);
		parent.placeActor(p);
	}
	
	/**
	 * Sets a series of parameters for this cannon actor. This
	 * below specifies the list of parameters:
	 * <table>
	 *   <tr>
	 *     <th>Name</th>
	 *     <th>Type</th>
	 *     <th>Description</th>
	 *   </tr>
	 *   <tr>
	 *     <td><code>heading</code></td>
	 *     <td><code>int</code></td>
	 *     <td>This sets the facing direction of the actor. </td>
	 *   </tr>
	 *   <tr>
	 *     <td><code>period</code></td>
	 *     <td><code>int</code></td>
	 *     <td>This sets number of big frames to wait in between 
	 *     shooting consecutive projectiles</td>
	 *   </tr>
	 * </table>
	 * @param params the parameter object.
	 */
	@Override
	public void setParameters(Map<String, Object> params)
	{
		super.setParameters(params);
		Object val = params.get("period");
		if (val != null && val instanceof Integer)
			setPeriod((Integer)val);
	}

	/**
	 * @return the period of firing one projectile
	 */
	public int getPeriod()
	{
		return period;
	}

	/**
	 * @param period the new period of firing one projectile
	 * @throws IllegalArgumentException if period is negative.
	 */
	public void setPeriod(int period)
	{
		if (period < 0)
			throw new IllegalArgumentException("Illegal period value");
		this.period = period;
	}
	
	/**
	 * Updates new frame to spawn some projectiles!
	 * @param frame the current frame number.
	 */
	@Override
	public void updateFrame(long frame)
	{
		super.updateFrame(frame);
		if (period != 0)
		{
			//frames must be more than the frames needed to move cannon one space.
			int framesPeriod = Math.max(BIG_FRAME * period, BIG_FRAME + 1);
			if (frame % framesPeriod == 0)
				shoot();
		}
		
	}
}
