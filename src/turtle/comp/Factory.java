package turtle.comp;

import static turtle.core.Actor.*;

import java.lang.reflect.Field;
import java.util.Map;

import javafx.scene.image.ImageView;
import turtle.core.*;
/**
 * Factory.java
 * 
 * This actor will attempt to clone a particular component over and over in its 
 * facing direction only when the linked button(s) is triggered.
 * 
 * @author Henry
 * Date: 5/9/17
 * Period: 2
 */
public class Factory extends Cell
{
	/** The default image for this component */
	public static final int DEFAULT_IMAGE = 61;
	
	private static final double RATIO_CLONE_IMG = .8;
	
	private boolean headingMatters = false;
	private int heading;
	private short componentCloned;
	
	private ImageView clonedImg;
	
	/**
	 * Constructs a new factory.
	 */
	public Factory()
	{
		heading = Actor.NORTH;
		headingMatters = false;
		componentCloned = -1;

		setImageFrame(DEFAULT_IMAGE);
		
		TileSet ts = getTileSet();
		double size = ts.getFrameSize() * RATIO_CLONE_IMG;
		
		clonedImg = new ImageView();
		clonedImg.setFitHeight(size);
		clonedImg.setFitWidth(size);
		clonedImg.setImage(ts.getImageset());
		clonedImg.setViewport(ts.frameAt(-1));
		this.getChildren().add(clonedImg);
	}
	
	/**
	 * @return the current component id that is being cloned.
	 */
	public short getComponentCloned()
	{
		return componentCloned;
	}

	/**
	 * Changes the current component being cloned.
	 * @param componentCloned the component id to clone
	 * @throws IllegalArgumentException if the component id is not a valid 
	 * actor.
	 */
	//TODO: accept negative.
	public void setComponentCloned(short componentCloned)
	{
		Class<Component> comp = getTileSet().componentAt(componentCloned);
		if (!Actor.class.isAssignableFrom(comp))
			throw new IllegalArgumentException("Component is not an actor");
		
		this.componentCloned = componentCloned;
		headingMatters = testHeadingMatters(comp);
		setHeading(heading);
		
		try
		{
			Field img = comp.getDeclaredField("DEFAULT_IMAGE");
			img.setAccessible(true);
			clonedImg.setViewport(getTileSet().frameAt(img.getInt(null)));
		}
		catch (NoSuchFieldException | IllegalArgumentException | 
				IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Clones one actor in that particular direction.
	 */
	public void cloneActor()
	{
		Grid parent = getParentGrid();
		if (parent == null || componentCloned == -1)
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
		
		try
		{
			Class<Component> comp = getTileSet().componentAt(componentCloned);
			Actor clone = (Actor)comp.newInstance();
			clone.setHeading(heading);
			clone.getHeadLocation().setLocation(row, col);
			clone.getTrailingLocation().setLocation(row, col);
			parent.placeActor(clone);
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the current direction heading
	 */
	public int getHeading()
	{
		return heading;
	}

	/**
	 * This sets the direction the factory is facing, and also rotates the
	 * factory to that direction. This also determines the direction the
	 * factory will clone its actors.
	 * @param heading the new direction heading to set
	 * @throws IllegalArgumentException if a illegal direction is given.
	 */
	public void setHeading(int heading)
	{
		if (heading < NORTH || heading > WEST)
			throw new IllegalArgumentException("Illegal direction");
		if (!headingMatters)
			clonedImg.setRotate(-heading * RIGHT_ANGLE);
		else
			clonedImg.setRotate(0);
		setRotate(heading * RIGHT_ANGLE);
		this.heading = heading;
	}
	
	/**
	 * Sets a series of parameters for an factory. This
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
	 *     <td>This sets the facing direction of the factory. </td>
	 *   </tr>
	 *   <tr>
	 *     <td><code>cloned</code></td>
	 *     <td><code>int</code></td>
	 *     <td>This sets the component id to clone, or -1 to clone nothing</td>
	 *   </tr>
	 * </table>
	 * @param params the parameter object.
	 */
	@Override
	public void setParameters(Map<String, Object> params)
	{
		super.setParameters(params);
		Object val = params.get("heading");
		//TODO: replace Integer with Number.
		if (val != null && val instanceof Integer)
		{
			int dir = (Integer)val;
			if (dir >= NORTH && dir <= WEST)
				setHeading(dir);
		}
		
		val = params.get("cloned");
		if (val != null && val instanceof Number)
		{
			short id = ((Number)val).shortValue();
			TileSet ts = getTileSet();
			if (id >= -1 && id < ts.getComponentCount())
			{
				Class<Component> comp = getTileSet().componentAt(id);
				if (Actor.class.isAssignableFrom(comp))
					setComponentCloned(id);
			}
		}
	}

	/**
	 * Checks if an actor can pass this location. This never lets anything
	 * pass through.
	 * @param visitor actor visiting the factory.
	 * @return false always to deny passing.
	 */
	@Override
	public boolean checkPass(Actor visitor)
	{
		return false;
	}

	/**
	 * Checks if an actor can pass this location. This never lets anything
	 * pass through.
	 * @param visitor actor visiting the factory.
	 * @return false always to deny passing.
	 */
	@Override
	public boolean pass(Actor visitor)
	{
		return false;
	}
	
	/**
	 * Tests whether if heading matters with a particular actor.
	 * Assumes that this class is an actor.
	 * @return true if it matters, false if it doesn't.
	 */
	private boolean testHeadingMatters(Class<Component> actor)
	{
		Actor a;
		try
		{
			a = (Actor)actor.newInstance();
			int before = a.getHeading();
			if (before == NORTH)
				a.setHeading(EAST);
			else 
				a.setHeading(NORTH);
			return a.getHeading() != before;
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
			return false;
		}
		
	}
}
