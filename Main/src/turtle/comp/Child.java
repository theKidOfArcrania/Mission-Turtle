package turtle.comp;

import turtle.core.Direction;
import turtle.core.Grid;
import turtle.core.Location;

import java.util.HashMap;
import java.util.Map;

/**
 * This will at first move at half speed with random direction, but once see's player in
 * line, it will charge double speed in that direction until it hits a wall.
 *
 * @author Henry
 */
public class Child extends Enemy {
    public static final int DEFAULT_IMAGE = 62;

    private static final int WALK_FRAMES[] = {63, 62};

    private static final int CHARGE_DIST = 10;
    private static final long serialVersionUID = -5302146650460128654L;

    /**
     * Determines the associated attributes with a tile if the tile is related to this object.
     * @param tileInd the index of the tile within a tileset
     * @return an mapping of the default attributes, or null if it is not related.
     */
    public static Map<String, ?> attributeOfTile(int tileInd) {
        if (tileInd >= DEFAULT_IMAGE && tileInd <= WALK_FRAMES[0]) {
            return new HashMap<>();
        }
        return null;
    }

    private long lastMove;
    private boolean frenzyState;
    private boolean moving;

    /**
     * Constructs a new child object.
     */
    public Child() {
        moving = false;
    }

    /**
     * Obtains the child's speed. It will double when child is in frenzy state, half
     * if child is in relaxed state.
     *
     * @return a speed in pixels per frame.
     */
    @Override
    public double getMoveSpeed() {
        double normal = super.getMoveSpeed();
        if (frenzyState) {
            return normal * 2;
        } else {
            return normal / 2;
        }
    }

    /**
     * Update frames so to update child's moving frame
     */
    @Override
    public void updateFrame(long frame) {
        super.updateFrame(frame);
        Grid g = getParentGrid();
        if (g == null) {
            return;
        }

        if (moving ^ isMoving()) {
            moving = !moving;
            if (moving) {
                animateFrames(WALK_FRAMES, true);
            } else {
                setImageFrame(DEFAULT_IMAGE);
            }
        }

        if (isMoving()) {
            return;
        }

        if (frenzyState) {
            if (traverseDirection(getHeading())) {
                lastMove = frame;
            } else {
                frenzyState = false;
            }
        } else {
            if (frame - lastMove == BIG_FRAME * 2) {
                Location playerLoc = g.getPlayer().getHeadLocation();
                Location loc = getHeadLocation();

                int dr = playerLoc.getRow() - loc.getRow();
                int dc = playerLoc.getColumn() - loc.getColumn();
                int dar = Math.abs(dr);
                int dac = Math.abs(dc);
                if (dr * dc == 0 && dar + dac <= CHARGE_DIST) {
                    frenzyState = true;
                    setHeading(dr, dc);
                    return;
                }
            }

            Direction[] choices = Direction.values();
            shuffle(choices, g.getRNG());
            Direction lastDir = Direction.NORTH;
            for (Direction dir : choices) {
                lastDir = dir;
                if (traverseDirection(dir)) {
                    lastMove = frame;
                    break;
                }
            }
            setHeading(lastDir);
        }
    }

    /**
     * Sets a heading based on a delta of row/column
     *
     * @param dr the delta of row (player - this)
     * @param dc the delta of column (player - this)
     */
    private void setHeading(int dr, int dc) {
        if (dc == 0) {
            if (dr > 0) {
                setHeading(Direction.SOUTH);
            } else {
                setHeading(Direction.NORTH);
            }
        } else {
            if (dc > 0) {
                setHeading(Direction.EAST);
            } else {
                setHeading(Direction.WEST);
            }
        }
    }
}
