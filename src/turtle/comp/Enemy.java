package turtle.comp;

import turtle.core.Actor;
import turtle.core.DominanceLevel;

/**
 * Enemy.java
 * <p>
 * This represents any actors that are hostile to the player, and will kill
 * the player on contact.
 *
 * @author Henry Wang
 *         Period: 2
 *         Date: 5/2/17
 */
public abstract class Enemy extends Actor
{

    /**
     * Checks whether an interaction with another actor is possible.
     * This only let players go through.
     *
     * @param other the other actor to interact with.
     * @return true to allow interaction, false to deny interaction.
     */
    public boolean checkInteract(Actor other)
    {
        return other instanceof Player;
    }

    /**
     * Interacts with other actors. By default, this will
     * kill the player (let player pass here), but to others,
     * it is a wall-like object.
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
            other.die(this);
            return true;
        }
        return false;
    }

    /**
     * Obtains the dominance level of an enemy. Typically
     * enemies have a slightly higher dominance level compared
     * to player.
     *
     * @param other the other actor to compare with.
     * @return dominance level.
     */
    @Override
    public DominanceLevel dominanceLevelFor(Actor other)
    {
        return ENEMY;
    }

}
