package turtle.comp;

import turtle.core.Direction;
import turtle.core.Location;

/**
 * Bird.java
 * <p>
 * This is an enemy that directly chases after the player. However, it will
 * not ever decide to walk around obstacles.
 *
 * @author Henry
 *         Date: 5/6/17
 *         Period: 2
 */
public class Bird extends Enemy
{
    public static final int DEFAULT_IMAGE = 52;
    private static final int BIRD_STILL_IMAGE = DEFAULT_IMAGE;
    private static final int BIRD_FLYING_IMAGE = 53;

    private static final long serialVersionUID = -387220900586289450L;


    /**
     * Updates frames so that the bird will change between flying/ still
     * depending whether if bird is moving or not. This will also move the bird
     * in the direction of the player.
     *
     * @param frame the frame number
     */
    @Override
    public void updateFrame(long frame)
    {
        super.updateFrame(frame);
        if (!isMoving())
        {
            Player p = getParentGrid().getPlayer();
            if (p != null)
            {
                Direction[] movement = calculateDirection();
                boolean moved = false;
                for (Direction dir : movement)
                {
                    if (traverseDirection(dir))
                    {
                        setImageFrame(BIRD_FLYING_IMAGE);
                        setHeading(dir);
                        moved = true;
                        break;
                    }
                }
                if (!moved)
                {
                    setImageFrame(BIRD_STILL_IMAGE);
                    setHeading(Direction.NORTH);
                }
            }
        }
    }

    /**
     * Calculates which direction to move towards. This will
     * calculate a priority of directions to move into.
     *
     * @return a list of possible directions to move into.
     */
    private Direction[] calculateDirection()
    {
        Player player = getParentGrid().getPlayer();
        if (player == null)
            return new Direction[0];

        Location playerLoc = player.getHeadLocation();
        Location loc = getHeadLocation();
        if (!playerLoc.isValidLocation() || !loc.isValidLocation())
            return new Direction[0];

        int dr = playerLoc.getRow() - loc.getRow();
        int dc = playerLoc.getColumn() - loc.getColumn();

        Direction rowDir = null;
        Direction colDir = null;

        if (dr < 0)
            rowDir = Direction.NORTH;
        else if (dr > 0)
            rowDir = Direction.SOUTH;

        if (dc < 0)
            colDir = Direction.WEST;
        else if (dc > 0)
            colDir = Direction.EAST;

        if (rowDir == null && colDir == null)
            return new Direction[0];

        if (rowDir == null)
            return new Direction[]{colDir};
        if (colDir == null)
            return new Direction[]{rowDir};

        if (Math.abs(dr) > Math.abs(dc))
        {
            return new Direction[]{rowDir, colDir};
        } else
            return new Direction[]{colDir, rowDir};
    }
}
