package turtle.comp;

import turtle.core.Actor;
import turtle.core.Cell;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This kills all actor visitors by the force of fire!
 *
 * @author Henry Wang
 */
public class Fire extends Cell {
    /**
     * The default image for this component
     */
    public static final int DEFAULT_IMAGE = 18;
    private static final int[] ANIMATE_FRAMES = {18, 19, 20};
    private static final int[] TRANSFORM_FRAMES = {19, 21, 22, 23, 24, 25, 26};
    private static final long serialVersionUID = 8374356983860533042L;

    private boolean smoking;

    /**
     * Determines the associated attributes with a tile if the tile is related to this object.
     * @param tileInd the index of the tile within a tileset
     * @return an mapping of the default attributes, or null if it is not related.
     */
    public static Map<String, ?> attributeOfTile(int tileInd) {
        if (tileInd >= DEFAULT_IMAGE && tileInd <= TRANSFORM_FRAMES[TRANSFORM_FRAMES.length - 1]) {
            return new HashMap<>();
        }
        return null;
    }

    /**
     * Constructs a new fire cell by initializing UI.
     */
    public Fire() {
        int[] randomized = ANIMATE_FRAMES.clone();
        shuffle(randomized, new Random());
        animateFrames(randomized, true);

        smoking = false;
    }

    /**
     * Checks whether if a pass to this cell is ever possible. Fire
     * will kill everything but it will first let it pass through.
     *
     * @param visitor the actor passing this cell.
     * @return always returns true to allow visitor to pass cell
     */
    @Override
    public boolean checkPass(Actor visitor) {
        return true;
    }

    /**
     * Kills all actors that visit this fire
     *
     * @param visitor the actor that comes over this cell.
     * @return true always, since they are permitted to come here.
     */
    @Override
    public boolean pass(Actor visitor) {
        if (smoking) {
            return true;
        }
        if (visitor.die(this)) {
            playSound(Sounds.STEAM);
            Sounds.GRASS.play();
        }
        return true;
    }

    /**
     * Transforms this fire into just sand.
     */
    public void transformToSand() {
        smoking = true;
        animateFrames(TRANSFORM_FRAMES, false);
        transformTo(new Sand(), DEF_ANIMATION_FRAME_CHANGE * TRANSFORM_FRAMES.length);
    }
}
