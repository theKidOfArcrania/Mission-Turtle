package turtle.comp;

import turtle.attributes.NotAttribute;
import turtle.core.*;

/**
 * Button.java
 * When linked to a factory, triggers a clone whenever an actor steps on this
 * tile.
 *
 * @author Henry Wang
 *         Date: 5/9/17
 *         Period: 2
 */
public class Button extends Actor
{
    /**
     * The default image for this component
     */
    public static final int DEFAULT_IMAGE = 64;
    private static final int BUTTON_OFFSET_IMAGE = 64;

    private Location linkedLocation;
    private ColorType color;

    /**
     * Constructs a new button and initializes image
     */
    public Button()
    {
        setImageFrame(DEFAULT_IMAGE);
        linkedLocation = new Location();
        setColor(ColorType.RED);
    }


    /**
     * @return the location of the linked factory (if any exists there).
     */
    public Location getLinked()
    {
        return linkedLocation;
    }

    /**
     * @param linkedLocation the new location to link to
     */
    public void setLinked(Location linkedLocation)
    {
        this.linkedLocation = linkedLocation;
    }

    /**
     * Interacts with other actors, triggering the associated factory (if any).
     *
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
                ((Factory) factory).cloneActor();
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
    public boolean checkInteract(Actor other)
    {
        return true;
    }

    /**
     * This overrides the Actor's setHeading since a heading does not
     * mean anything for this actor.
     */
    @Override
    @NotAttribute
    public void setHeading(int heading)
    {
        //Does nothing
    }

    /**
     * @return the color of this button
     */
    public ColorType getColor()
    {
        return color;
    }

    /**
     * @param color the new color to set for this button
     * @throws NullPointerException if the color supplied is null.
     */
    public void setColor(ColorType color)
    {
        if (color == null)
            throw new NullPointerException();
        setImageFrame(color.getImageFrame(BUTTON_OFFSET_IMAGE));
        this.color = color;
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


}
