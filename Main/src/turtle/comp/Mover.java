package turtle.comp;

import turtle.core.*;

/**
 * Represents any object that can be physically moved by a player.
 *
 * @author Henry Wang
 */
public abstract class Mover extends Actor {

    private static final long serialVersionUID = 2633910574669753448L;

    /**
     * Checks whether an interaction with another actor is possible.
     *
     * @param other the other actor to interact with.
     * @return true if the other actor can pass into location
     * false if other actor is prohibited to pass.
     */
    @Override
    public boolean checkInteract(Actor other) {
        if (isDying()) {
            return true;
        }

        if (!(other instanceof Player)) {
            return false;
        }

        Direction dir = getPlayerVector();
        return dir != null && traverseDirection(dir, false);
    }

    /**
     * Executes an interaction with another actor. This will move in the
     * specified vector of the player. In other words, this will execute
     * the "push" that the player has executed on this actor. Only the
     * player can move this actor. Other actors, including other movers
     * may not move each other.
     *
     * @param other the other actor to interact with.
     * @return true if the other actor can pass into location
     * false if other actor is prohibited to pass.
     */
    @Override
    public boolean interact(Actor other) {
        if (isDying()) {
            return true;
        }

        if (!(other instanceof Player)) {
            return false;
        }

        Direction dir = getPlayerVector();
        return dir != null && traverseDirection(dir);
    }

    /**
     * Obtains the dominance level of a mover. Movers should be near the top of
     * the z-order, therefore in such a case, when calculating z-order,
     * the mover will return an artificially low dominance level. However,
     * in other cases, it will have a medium dominance level (Mover).
     *
     * @param other the other actor to compare with.
     * @return dominance level of a mover.
     */
    @Override
    public DominanceLevel dominanceLevelFor(Actor other) {
        if (other == null) {
            return FLOATING;
        } else {
            return MOVER;
        }
    }

    /**
     * Obtains the vector the player is pushing against this mover at, based
     * on the player's relative position to this mover.
     *
     * @return directionals 0 to 3, or -1 if there is no vector.
     */
    protected Direction getPlayerVector() {
        Grid parent = getParentGrid();
        if (parent == null) {
            return null;
        }

        final int poss[] = {-1, 0, 0, 1, 1, 0, 0, -1};

        Location playerLoc = parent.getPlayer().getHeadLocation();
        Location blockLoc = getHeadLocation();

        int dr = blockLoc.getRow() - playerLoc.getRow();
        int dc = blockLoc.getColumn() - playerLoc.getColumn();

        int dir;
        for (dir = 0; dir < poss.length; dir += 2) {
            if (poss[dir] == dr && poss[dir + 1] == dc) {
                break;
            }
        }

        if (dir >= poss.length) {
            return null;
        } else {
            return Direction.values()[dir / 2];
        }
    }
}
