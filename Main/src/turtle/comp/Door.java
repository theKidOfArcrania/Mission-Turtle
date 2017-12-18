package turtle.comp;

import turtle.attributes.NotAttribute;
import turtle.core.Actor;
import turtle.core.Component;
import turtle.core.Direction;
import turtle.core.DominanceLevel;

/**
 * This acts as a wall until an actor passes through this door, which contains a
 * color-matching key. At this point the actor will remove itself.
 *
 * @author Henry Wang
 */
public class Door extends Actor {
    public static final int DEFAULT_IMAGE = 0;
    private static final int LOCK_OFFSET_IMAGE = DEFAULT_IMAGE;
    private static final long serialVersionUID = 4234019785119559213L;

    private ColorType color;

    /**
     * Constructs a new door, defaulting to the RED color.
     */
    public Door() {
        setColor(ColorType.YELLOW);
    }

    /**
     * Overrides dying so that it doesn't die from anything,
     * as this is a fixture. (Only "die" when we unlocked gate,
     * represented by killing itself.)
     *
     * @param attacker the component who is attacking.
     * @return false always since it doesn't die.
     */
    @Override
    public boolean die(Component attacker) {
        return attacker == this && super.die(attacker);
    }

    /**
     * Checks whether an interaction with another actor is possible.
     * This checks if the actor contains a key of this same color to this door.
     *
     * @param other the other actor to interact with.
     * @return true if the other actor can pass into location
     * false if other actor is prohibited to pass.
     */
    @Override
    public boolean checkInteract(Actor other) {
        if (other instanceof Player) {
            for (Item itm : ((Player) other).getPocket()) {
                if (itm instanceof Key && ((Key) itm).getColor() == getColor()) {
                    die(this);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Obtains the dominance level of the actor in relation to another actor.
     * This will be high on the dominance level since it is a fixture.
     *
     * @param other other actor to compare with (or null for generally).
     * @return a dominance level of the actor.
     */
    @Override
    public DominanceLevel dominanceLevelFor(Actor other) {
        return FIXTURE;
    }

    /**
     * @return the color of this door
     */
    public ColorType getColor() {
        return color;
    }

    /**
     * @param color the new color to set for this door
     * @throws NullPointerException if the color supplied is null.
     */
    public void setColor(ColorType color) {
        if (color == null) {
            throw new NullPointerException();
        }
        setImageFrame(color.getImageFrame(LOCK_OFFSET_IMAGE));
        this.color = color;
    }

    /**
     * Executes an interaction with another actor. This will only
     * allow the pass if an actor has a color-matching key. This
     * will subsequently take away that key used to open this door.
     *
     * @param other the other actor to interact with.
     * @return true if the other actor can pass into location
     * false if other actor is prohibited to pass.
     */
    @Override
    public boolean interact(Actor other) {
        if (other instanceof Player) {
            Item itm = ((Player) other).useItem(
                    t -> t instanceof Key && ((Key) t).getColor() == getColor());
            return itm != null;
        }
        return false;
    }

    /**
     * This overrides the Actor's setHeading since a heading does not
     * mean anything for this actor.
     *
     * @return always north (since it is facing that direction always).
     */
    @Override
    @NotAttribute
    public Direction getHeading() {
        return Direction.NORTH;
    }

    /**
     * This overrides the Actor's setHeading since a heading does not
     * mean anything for this actor.
     *
     * @param heading the direction of heading
     */
    @Override
    @NotAttribute
    public void setHeading(Direction heading) {
        //Does nothing
    }
}
