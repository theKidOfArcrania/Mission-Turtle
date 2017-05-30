package turtle.comp;

import turtle.core.Direction;
import turtle.core.Grid;
import turtle.core.Location;

/**
 * Cannon.java
 * <p>
 * This will shoot cannon projectiles at fixed interval times that can be
 * configured as a period. By itself it can be moved around much similarly
 * to the {@link Bucket} class.
 *
 * @author Henry
 *         Date: 5/9/17
 *         Period: 2
 */
public class Cannon extends Mover
{
    /**
     * The default image for this component
     */
    public static final int DEFAULT_IMAGE = 35;

    private static final int DEFAULT_SHOOTING_PERIOD = 2;
    private static final int[] SHOOTING_ANIMATION = {36, 37, 38, 35};
    private static final long serialVersionUID = 705243324532344078L;

    private int period;

    /**
     * Constructs a new cannon and sets up image.
     */
    public Cannon()
    {
        period = DEFAULT_SHOOTING_PERIOD;
    }

    /**
     * Fires a projectile in cannon's facing direction.
     */
    private void shoot()
    {
        Grid parent = getParentGrid();
        if (parent == null)
            return;

        Direction heading = getHeading();
        Location loc = new Location(getHeadLocation());
        heading.traverse(loc);

        animateFrames(SHOOTING_ANIMATION, false);

        Projectile p = new Projectile();
        p.setHeading(heading);
        p.getHeadLocation().setLocation(loc);
        p.getTrailingLocation().setLocation(loc);
        parent.placeActor(p);
    }

    /**
     * @return the period of firing one projectile
     */
    public int getPeriod()
    {
        return period;
    }

    /**
     * @param period the new period of firing one projectile
     * @throws IllegalArgumentException if period is negative.
     */
    public void setPeriod(int period)
    {
        if (period < 0)
            throw new IllegalArgumentException("Illegal period value");
        this.period = period;
    }

    /**
     * Updates new frame to spawn some projectiles!
     *
     * @param frame the current frame number.
     */
    @Override
    public void updateFrame(long frame)
    {
        super.updateFrame(frame);
        if (period != 0)
        {
            int framesPeriod = BIG_FRAME * period;
            if (frame % framesPeriod == 0)
                shoot();
        }

    }
}
