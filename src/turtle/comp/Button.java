package turtle.comp;

import java.util.Map;

import turtle.core.*;

/**
 * Button.java
 * When linked to a factory, triggers a clone whenever an actor steps on this 
 * tile.
 * 
 * @author Henry Wang
 * Date: 5/9/17
 * Period: 2
 */
public class Button extends Actor
{
	/** The default image for this component */
	public static final int DEFAULT_IMAGE = 60;
	
	private Location linkedLocation;
	
	/**
	 * Constructs a new button and initializes image
	 */
	public Button()
	{
		setImageFrame(DEFAULT_IMAGE);
		linkedLocation = new Location();
	}

	
	/**
	 * @return the location of the linked factory (if any exists there).
	 */
	public Location getLinkedLocation()
	{
		return linkedLocation;
	}

	/**
	 * Determines that this is a static element. 
	 * @return false, b/c this is a static element.
	 */
	@Override
	public boolean isActiveElement()
	{
		return false;
	}
	
	/**
	 * Interacts with other actors, triggering the associated factory (if any).
	 * @param other other actor to interact with.
	 * @return always returns true to allow anything to pass through it.
	 */
	@Override
	public boolean interact(Actor other)
	{
		Grid parent = getParentGrid();
		if (parent != null && parent.isValidLocation(linkedLocation))
		{
			Cell factory = parent.getCellAt(linkedLocation);
			if (factory instanceof Factory)
				((Factory)factory).cloneActor();
		}
		return true;
	}

	/**
	 * Checks whether an interaction with another actor is possible.
	 * This will always let actors pass through
	 * 
	 * @param other the other actor to interact with.
	 * @return true to always allow others to enter.
	 */
	public boolean checkInteract(Actor other) {
		return true;
	}
	
	/**
	 * This overrides the Actor's setHeading since a heading does not
	 * mean anything for this actor.
	 */
	@Override
	public void setHeading(int heading)
	{
		//Does nothing
	}
	
	/**
	 * Overrides dying so that it doesn't die from anything,
	 * as this is a fixture.
	 * 
	 * @param attacker the component who is attacking.
	 * @return false always since it doesn't die.
	 */
	@Override
	public boolean die(Component attacker)
	{
		return false;
	}
	
	/**
	 * Obtains the dominance level of the actor in relation to another actor.
	 * This will always return FIXTURE; it is the highest on dominance level.
	 * 
	 * @param other other actor to compare with (or null for generally).
	 * @return a dominance level of the actor.
	 */
	@Override
	public DominanceLevel dominanceLevelFor(Actor other)
	{
		return FIXTURE;
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
	 *     <td><code>linked</code></td>
	 *     <td><code>Location</code></td>
	 *     <td>This sets the linked location of button. </td>
	 *   </tr>
	 * </table>
	 * @param params the parameter object.
	 */
	@Override
	public void setParameters(Map<String, Object> params)
	{
		super.setParameters(params);
		Object val = params.get("linked");
		if (val != null && val instanceof Location)
			linkedLocation.setLocation((Location)val);
	}

}
