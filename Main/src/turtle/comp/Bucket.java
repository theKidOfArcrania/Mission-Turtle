package turtle.comp;

import turtle.core.Component;
import turtle.core.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This actor can interacts in the game by filling up with water in water cells
 * and quells fire cells.
 *
 * @author Henry
 */
public class Bucket extends Mover {
    public static final int DEFAULT_IMAGE = 31;
    private static final int[] ANIMATE_FRAMES = {32, 33, 34};
    private static final long serialVersionUID = 3202659903623058965L;

    /**
     * Determines the associated attributes with a tile if the tile is related to this object.
     * @param tileInd the index of the tile within a tileset
     * @return an mapping of the default attributes, or null if it is not related.
     */
    public static Map<String, ?> attributeOfTile(int tileInd) {
        if (tileInd >= DEFAULT_IMAGE && tileInd < DEFAULT_IMAGE + ANIMATE_FRAMES.length) {
            HashMap<String, Object> vals = new HashMap<>();
            vals.put("filled", tileInd != DEFAULT_IMAGE);
            return vals;
        }
        return null;
    }

    private boolean filled;

    /**
     * Overrides die procedure, so that bucket will fill on contact of water
     * and it will quell fire if it is filled.
     *
     * @param attacker the component attacking bucket.
     * @return true if this died as a result of call.
     */
    @Override
    public boolean die(Component attacker) {
        if (attacker instanceof Water) {
            if (!isFilled()) {
                setFilled(true);
                ((Water) attacker).transformToSand();
            }
            return false;
        } else if (attacker instanceof Fire) {
            if (isFilled()) {
                ((Fire) attacker).transformToSand();
            }
        }
        return super.die(attacker);
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
     * @return whether if this bucket is filled with water.
     */
    public boolean isFilled() {
        return filled;
    }

    /**
     * Sets whether if bucket is filled and edits image.
     *
     * @param filled true to be filled with water, false if empty.
     */
    public void setFilled(boolean filled) {
        this.filled = filled;
        if (filled) {
            int[] randomized = ANIMATE_FRAMES.clone();
            shuffle(randomized, new Random());
            animateFrames(randomized, true);
        } else {
            setImageFrame(DEFAULT_IMAGE);
        }
    }


}
