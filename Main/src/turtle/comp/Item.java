package turtle.comp;

import turtle.core.Actor;
import turtle.core.Component;
import turtle.core.Direction;
import turtle.core.DominanceLevel;

/**
 * Represents a group of stuff that a player can collect.
 *
 * @author Henry Wang
 */
public abstract class Item extends Actor {

    private static final long serialVersionUID = -6949433579943995314L;

    /**
     * Interacts with other actors, allowing the player to pick up this item.
     *
     * @param other other actor to interact with.
     * @return always returns true to allow anything to pass through it.
     */
    @Override
    public boolean interact(Actor other) {
        if (other instanceof Player) {
            //if (getCurrentClip() == null)
                playSound(Sounds.TAP);
            Player p = (Player) other;
            if (p.collectItem(this)) {
                getParentGrid().removeActor(this);
            }
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
     *
     * @return always north (since it is facing that direction always).
     */
    @Override
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
    public void setHeading(Direction heading) {
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
    public boolean die(Component attacker) {
        return false;
    }

    /**
     * Obtains the dominance level of the actor in relation to another actor.
     * This will always return ITEM, so it is pretty high on the
     * dominance level list.
     *
     * @param other other actor to compare with (or null for generally).
     * @return a dominance level of the actor.
     */
    @Override
    public DominanceLevel dominanceLevelFor(Actor other) {
        return ITEM;
    }

    /**
     * Checks whether if this item is identical as another.
     *
     * @param other other item to compare with
     * @return true if both items are identical
     */
    public abstract boolean identical(Item other);

    /**
     * Obtains the index of the image to display as an item.
     *
     * @return the index of image as item.
     */
    public abstract int getItemImage();
}
