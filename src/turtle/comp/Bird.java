package turtle.comp;

import turtle.core.Location;

/**
 * Bird.java
 * 
 * This is an enemy that directly chases after the player. However, it will 
 * not ever decide to walk around obstacles.
 * 
 * @author Henry
 * Date: 5/6/17
 * Period: 2
 */
public class Bird extends Enemy
{
	/** The default image for this component */
	public static final int DEFAULT_IMAGE = 52;
	
	private static final int BIRD_STILL_IMAGE = 52;
	private static final int BIRD_FLYING_IMAGE = 53;
	
	
	/**
	 * Creates a new bird and initializes image.
	 */
	public Bird()
	{
		setImageFrame(BIRD_STILL_IMAGE);
	}
	
	/**
	 * Updates frames so that the bird will change between flying/ still
	 * depending whether if bird is moving or not. This will also move the bird 
	 * in the direction of the player. 
	 * @param frame the frame number
	 */
	@Override
	public void updateFrame(long frame)
	{
		super.updateFrame(frame);
		if (!isMoving())
		{
			Player p = getParentGrid().getPlayer();
			if (p != null)
			{
				int[] movement = calculateDirection(); //TODO: calculate direction.
				boolean moved = false;
				for (int dir : movement)
				{
					if (traverseDirection(dir))
					{
						setImageFrame(BIRD_FLYING_IMAGE);
						setHeading(dir);
						moved = true;
						break;
					}
				}
				if (!moved)
				{
					setImageFrame(BIRD_STILL_IMAGE);
					setHeading(NORTH);
				}
			}
		}
	}
	
	/**
	 * Calculates which direction to move towards. This will 
	 * calculate a priority of directions to move into. 
	 * @return a list of possible directions to move into.
	 */
	private int[] calculateDirection()
	{
		Player player = getParentGrid().getPlayer();
		if (player == null)
			return new int[0];
		
		Location playerLoc = player.getHeadLocation();
		Location loc = getHeadLocation();
		if (!playerLoc.isValidLocation() || !loc.isValidLocation())
			return new int[0];
		
		int dr = playerLoc.getRow() - loc.getRow();
		int dc = playerLoc.getColumn() - loc.getColumn();
		
		int rowDir = -1;
		int colDir = -1;
		
		if (dr < 0)
			rowDir = NORTH;
		else if (dr > 0)
			rowDir = SOUTH;
		
		if (dc < 0)
			colDir = WEST;
		else if (dc > 0)
			colDir = EAST;
		
		if (rowDir == -1 && colDir == -1)
			return new int[0];
		
		if (rowDir == -1)
			return new int[] {colDir};
		if (colDir == -1)
			return new int[] {rowDir};
		
		if (Math.abs(dr) > Math.abs(dc))
		{
			return new int[] {rowDir, colDir};
		}
		else
			return new int[] {colDir, rowDir};
	}
}
