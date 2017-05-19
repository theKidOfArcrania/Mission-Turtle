package turtle.comp;

import turtle.attributes.NotAttribute;
import turtle.core.Component;

import java.util.Map;
import java.util.Random;

/**
 * Bucket.java
 * This actor can interacts in the game by filling up with water in water cells
 * and quells fire cells.
 *
 * @author Henry
 *         Date: 5/9/17
 *         Period: 2
 */
public class Bucket extends Mover
{
    /**
     * The default image for this component
     */
    public static final int DEFAULT_IMAGE = 31;

    private static final int EMPTY_IMAGE = 31;
    private static final int[] ANIMATE_FRAMES = {32, 33, 34};

    private boolean filled;

    /**
     * Constructs a Bucket and initializes image.
     */
    public Bucket()
    {
        setImageFrame(EMPTY_IMAGE);
    }

    /**
     * Overrides die procedure, so that bucket will fill on contact of water
     * and it will quell fire if it is filled.
     *
     * @param attacker the component attacking bucket.
     * @return true if this died as a result of call.
     */
    @Override
    public boolean die(Component attacker)
    {
        if (attacker instanceof Water)
        {
            if (!isFilled())
            {
                setFilled(true);
                ((Water) attacker).transformToSand();
            }
            return false;
        } else if (attacker instanceof Fire)
        {
            if (isFilled())
                ((Fire) attacker).transformToSand();
        }
        return super.die(attacker);
    }

    /**
     * This overrides the Actor's setHeading since a heading does not
     * mean anything for this actor.
     * @return always north (since it is facing that direction always).
     */
    @Override
    @NotAttribute
    public int getHeading()
    {
        return NORTH;
    }

    /**
     * This overrides the Actor's setHeading since a heading does not
     * mean anything for this actor.
     */
    @Override
    @NotAttribute
    public void setHeading(int heading)
    {
        //Does nothing
    }


    /**
     * @return whether if this bucket is filled with water.
     */
    public boolean isFilled()
    {
        return filled;
    }

    /**
     * Sets whether if bucket is filled and edits image.
     *
     * @param filled true to be filled with water, false if empty.
     */
    public void setFilled(boolean filled)
    {
        this.filled = filled;
        if (filled)
        {
            int[] randomized = ANIMATE_FRAMES.clone();
            shuffle(randomized, new Random());
            animateFrames(randomized, true);
        } else
            setImageFrame(EMPTY_IMAGE);
    }


}
