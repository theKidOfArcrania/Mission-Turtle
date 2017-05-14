package turtle.comp;

/**
 * LawnMower.java
 * Moves back and forth with no reason or rhyme. Kills turtles within the path.
 * 
 * @author Henry
 * Date: 5/10/17
 * Period: 2
 */
public class LawnMower extends Enemy
{
	/** The default image for this component */
	public static final int DEFAULT_IMAGE = 60;
	private static final int DIRECTIONS = 4;
	
	/**
	 * Creates a new lawn-mower
	 */
	public LawnMower()
	{
		setImageFrame(DEFAULT_IMAGE);
	}

	/**
	 * Updates frames so that the lawn-mower will move in its facing direction.
	 * If it is blocked by something, it will attempt to bounce back.
	 * @param frame the frame number
	 */
	@Override
	public void updateFrame(long frame)
	{
		super.updateFrame(frame);
		if (!isMoving())
		{
			final int[] options = {0, 2};
			int heading = getHeading();
			for (int turn : options)
			{
				int newDir = (turn + heading) % DIRECTIONS;
				if (traverseDirection(newDir, true) || 
						turn == options[options.length - 1])
				{
					setHeading(newDir);
					break;
				}
			}
		}
	}
}
