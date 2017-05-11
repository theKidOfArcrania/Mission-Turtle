package turtle.comp;

public class Baby extends Enemy
{
	/** The default image for this component */
	public static final int DEFAULT_IMAGE = 64;
	
	private static final int WALK_FRAMES[] = {65,64};
	
	public Baby()
	{
		setImageFrame(DEFAULT_IMAGE);
	}

}
