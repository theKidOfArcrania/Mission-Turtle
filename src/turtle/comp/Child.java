package turtle.comp;

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
	public static final int DEFAULT_IMAGE = 64;
	
	private static final int WALK_FRAMES[] = {65,64};
	
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
	 * 
	 */
	@Override
	public void updateFrame(long frame)
	{
		super.updateFrame(frame);
		if (frenzyState)
		{
			traverseDirection(getHeading());
		}
	}
}
