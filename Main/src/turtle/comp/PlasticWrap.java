package turtle.comp;

import turtle.attributes.NotAttribute;
import turtle.core.Component;
import turtle.core.Direction;
import turtle.core.Grid;

/**
 * Plastic wrap enemies will float around randomly in water at half speed.
 * Once it shores up on land, it becomes just stationary.
 *
 * @author Henry Wang
 */
public class PlasticWrap extends Enemy {
    public static final int DEFAULT_IMAGE = 11;

    private static final long serialVersionUID = 4903268062632526294L;

    /**
     * Kills this actor (this sets a flag for this actor to be removed).
     * This overrides it to be immune to water.
     *
     * @param attacker the thing that is killing this actor.
     * @return true if this actor died as a result of this call, false if
     * nothing changed.
     */
    @Override
    public boolean die(Component attacker) {
        return !(attacker instanceof Water) && super.die(attacker);
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

    /**
     * Overrides the move speed so that it moves half of the default speed.
     */
    @Override
    public double getMoveSpeed() {
        return super.getMoveSpeed() / 2;
    }

    /**
     * This updates the frame so that the plastic wrap will float around
     * if it is under water.
     */
    @Override
    public void updateFrame(long frame) {
        super.updateFrame(frame);
        Grid g = getParentGrid();
        if (g == null) {
            return;
        }

        if (!isMoving() && g.getCellAt(getHeadLocation()) instanceof Water) {
            Direction[] choices = Direction.values();
            shuffle(choices, g.getRNG());
            for (Direction dir : choices)
                if (traverseDirection(dir)) {
                    break;
                }
        }

    }
}
