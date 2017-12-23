package turtle.comp;

import turtle.core.Actor;
import turtle.core.Component;
import turtle.core.Direction;
import turtle.core.DominanceLevel;

import java.util.HashMap;
import java.util.Map;

/**
 * This actor will kill any victim that happens to step over this contraption.
 * The trap will then immediately disintegrate.
 *
 * @author Henry
 */
public class Trap extends Actor {
    public static final int DEFAULT_IMAGE = 56;
    private static final int[] ANIMATION_IMAGE = {57, 58, 59};
    private static final long serialVersionUID = -2850122197270893076L;


    /**
     * Determines the associated attributes with a tile if the tile is related to this object.
     * @param tileInd the index of the tile within a tileset
     * @return an mapping of the default attributes, or null if it is not related.
     */
    public static Map<String, ?> attributeOfTile(int tileInd) {
        if (tileInd >= DEFAULT_IMAGE && tileInd <= ANIMATION_IMAGE[ANIMATION_IMAGE.length - 1]) {
            return new HashMap<>();
        }
        return null;
    }

    /**
     * Checks if an actor can pass through this trap.
     *
     * @param other the actor to pass through
     * @return true always to allow passing into location
     */
    @Override
    public boolean checkInteract(Actor other) {
        return true;
    }

    /**
     * Interacts with other actors. This will kill everything
     * on contact. If the actor dies, this will also self-destruct itself.
     *
     * @param other the other actor to interact with.
     * @return true always to allow passing into location
     */
    @Override
    public boolean interact(Actor other) {
        if (other.die(this)) {
            die(this);
        }
        return true;
    }

    /**
     * Obtains dominance level for actor. This actor has a fixture
     * dominance level since it passively destroys many.
     *
     * @param other the other actor to compare with.
     * @return dominance level.
     */
    @Override
    public DominanceLevel dominanceLevelFor(Actor other) {
        return FIXTURE;
    }

    /**
     * Handles dying process. This will only die if it is triggered
     * by someone else. In that event, it will effectively kill itself.
     *
     * @param attacker the component that initiated the attack.
     * @return true if it dies, false otherwise.
     */
    @Override
    public boolean die(Component attacker) {
        if (attacker == this && super.die(attacker)) {
            animateFrames(ANIMATION_IMAGE, false);
            return true;
        }
        return false;
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
}
