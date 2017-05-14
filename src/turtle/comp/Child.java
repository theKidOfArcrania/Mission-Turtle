package turtle.comp;

import turtle.core.Grid;
import turtle.core.Location;

/**
 * Child.java
 * 
 * This will at first move at half speed with random direction, but once see's player in
 * line, it will charge double speed in that direction until it hits a wall.
 * 
 * @author Henry
 * Date 5/13/17
 * Period: 2
 * 
 */
public class Child extends Enemy
{
	/** The default image for this component */
	public static final int DEFAULT_IMAGE = 62;
	
	private static final int WALK_FRAMES[] = {63,62};

	private static final int CHARGE_DIST = 5;
	
	private long lastMove;
	private boolean frenzyState;
	
	/**
	 * Constructs a new child object.
	 */
	public Child()
	{
		setImageFrame(DEFAULT_IMAGE);
	}

	/**
	 * Obtains the child's speed. It will double when child is in frenzy state, half
	 * if child is in relaxed state.
	 * @return a speed in pixels per frame.
	 */
	@Override
	public double getMoveSpeed()
	{
		double normal = super.getMoveSpeed();
		if (frenzyState)
			return normal * 2;
		else
			return normal / 2;
	}
	
	/**
	 * Update frames so to update child's moving frame
	 */
	@Override
	public void updateFrame(long frame)
	{
		super.updateFrame(frame);
		Grid g = getParentGrid();
		if (g == null)
			return;
		if (isMoving())
			return;
		
		if (frenzyState)
		{
			if (traverseDirection(getHeading()))
				lastMove = frame;
			else
				frenzyState = false;
		}
		else
		{
			if (frame - lastMove > BIG_FRAME * 2)
			{
				Location playerLoc = g.getPlayer().getHeadLocation();
				Location loc = getHeadLocation();
				
				int dr = Math.abs(playerLoc.getRow() - loc.getRow());
				int dc = Math.abs(playerLoc.getColumn() - loc.getColumn());
				if (dr * dc == 0 && dr + dc <= CHARGE_DIST)
				{
					frenzyState = true;
					return;
				}
			}
			
			int[] choices = {NORTH, EAST, SOUTH, WEST};
			shuffle(choices, g.getRNG());
			int lastDir = NORTH;
			for (int dir : choices)
			{
				lastDir = dir;
				if (traverseDirection(dir))
				{
					lastMove = frame;
					break;
				}
			}
			setHeading(lastDir);
		}
	}
}
