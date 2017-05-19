package turtle.comp;

import turtle.attributes.NotAttribute;
import turtle.core.Actor;
import turtle.core.Component;
import turtle.core.DominanceLevel;

import java.util.Map;

/**
 * Hint.java
 * <p>
 * When a player hovers under this actor, the player will be able to
 * receive a message. More specifically, this hint tile will trigger a
 * hint flag in the player that will automatically disable after player
 * exits hint object.
 *
 * @author Henry Wang
 *         Period: 2
 *         Date: 5/6/17
 */
public class Hint extends Actor
{
    /**
     * The default image for this component
     */
    public static final int DEFAULT_IMAGE = 55;
    private static final int HINT_IMAGE = 55;

    private String message;
    private Player playerRead;

    /**
     * Creates a new Hint cell with blank message and initializes UI.
     */
    public Hint()
    {
        setImageFrame(HINT_IMAGE);
        playerRead = null;
        message = "";
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
     * This overrides the Actor's setHeading since a heading does not
     * mean anything for this actor.
     * @return always north (since it is facing that direction always).
     */
    @Override
    @NotAttribute
    public int getHeading()
    {
        return NORTH;
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
     * @return the message this hint will tell.
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Sets this hint to show a new message.
     *
     * @param message the new message to tell.
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * Executes an interaction with another actor. This will always
     * allow any actor to pass through. However, if the actor is a
     * player, we will inform the player of the hint.
     *
     * @param other the other actor to interact with.
     * @return true if the other actor can pass into location
     * false if other actor is prohibited to pass.
     */
    @Override
    public boolean interact(Actor other)
    {
        if (other instanceof Player)
        {
            Player p = (Player) other;
            playerRead = p;
            p.setMessage(message, this);
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
     * Obtains dominance level for actor. This actor will always be
     * a fixture dominance, having a relatively high dominance level.
     *
     * @param other the other actor to compare with.
     * @return dominance level.
     */
    @Override
    public DominanceLevel dominanceLevelFor(Actor other)
    {
        return FIXTURE;
    }

    /**
     * Updates animation frame of component. This will also check for
     * the player location whether if it has left the hint tile.
     *
     * @param frame the animation frame.
     */
    @Override
    public void updateFrame(long frame)
    {
        super.updateFrame(frame);
        if (playerRead != null && !playerRead.getHeadLocation().equals(
                getHeadLocation()))
        {
            playerRead.resetMessage(this);
            playerRead = null;
        }
    }


}
