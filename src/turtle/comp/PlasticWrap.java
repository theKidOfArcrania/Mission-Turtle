package turtle.comp;

import turtle.core.Component;
import turtle.core.Grid;

/**
 * PlasticWrap.java
 * Plastic wrap enemies will float around randomly in water at half speed.
 * Once it shores up on land, it becomes just stationary.
 * @author Henry Wang
 * Date: 5/10/17
 * Period: 2
 */
public class PlasticWrap extends Enemy
{
	/** The default image for this component */
	public static final int DEFAULT_IMAGE = 11;
	
	private static final int DIRECTIONS = 4;
	/**
	 * Creates a new Plastic-wrap enemy
	 */
	public PlasticWrap()
	{
		setImageFrame(DEFAULT_IMAGE);
	}
	

	/**
	 * Kills this actor (this sets a flag for this actor to be removed).
	 * This overrides it to be immune to water. 
	 * 
	 * @param attacker the thing that is killing this actor.
	 * @return true if this actor died as a result of this call, false if
	 * 		nothing changed.
	 */
	@Override
	public boolean die(Component attacker)
	{
		if (attacker instanceof Water)
			return false;
		return super.die(attacker);
	}
	
	/**
	 * Overrides the move speed so that it moves half of the default speed.
	 */
	@Override
	public double getMoveSpeed()
	{
		// TODO Auto-generated method stub
		return super.getMoveSpeed() / 2;
	}
	
	/**
	 * This updates the frame so that the plastic wrap will float around
	 * if it is under water.
	 */
	@Override
	public void updateFrame(long frame)
	{
		super.updateFrame(frame);
		Grid g = getParentGrid();
		if (g == null)
			return;
		
		if (!isMoving() && g.getCellAt(getHeadLocation()) instanceof Water)
			traverseDirection((g.getRNG().nextInt(DIRECTIONS)));
	}
}
