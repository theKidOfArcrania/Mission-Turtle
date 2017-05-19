package turtle.comp;

import turtle.core.Actor;
import turtle.core.DominanceLevel;

/**
 * Projectile.java
 * <p>
 * This is an enemy actor that will kill everything in its path. If it
 * encounters wall-like objects, it will try to optimally turn around
 * (right, then left, then back out).
 *
 * @author Henry
 */
public class Projectile extends Enemy
{
    public static final int DEFAULT_IMAGE = 39;

    /**
     * Constructs a projectile.
     */
    public Projectile()
    {
        setImageFrame(DEFAULT_IMAGE);
    }

    /**
     * Kills everything in its path. If successful in killing, it will die as
     * well.
     *
     * @param other other actor to interact with
     * @return true, always allows interactions.
     */
    @Override
    public boolean interact(Actor other)
    {
        if (other.die(this))
            die(this);
        return true;
    }

    /**
     * Checks whether an interaction with another actor is possible.
     * This will let everything through.
     *
     * @param other the other actor to interact with.
     * @return true to always allow interaction.
     */
    @Override
    public boolean checkInteract(Actor other)
    {
        return true;
    }

    /**
     * Updates frames so that the projectile will move in its facing direction.
     * If per-say, it is blocked by something, it will attempt to turn
     *
     * @param frame the frame number
     */
    @Override
    public void updateFrame(long frame)
    {
        super.updateFrame(frame);
        if (!isMoving())
        {
            final int[] options = {0, 1, 3, 2};
            int heading = getHeading();
            for (int turn : options)
            {
                int newDir = (turn + heading) % options.length;
                if (traverseDirection(newDir, true) ||
                        turn == options[options.length - 1])
                {
                    setHeading(newDir);
                    break;
                }
            }
        }
    }
}
