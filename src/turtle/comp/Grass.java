package turtle.comp;

import turtle.core.*;

/**
 * Grass.java
 * <p>
 * This will conceal everything so that the player will not be able to see the underneath
 * stuff. When the player comes within one space of the grass, the grass will fade away.
 *
 * @author Henry
 */
public class Grass extends Actor
{

    public static final int DEFAULT_IMAGE = 6;
    private static final int[] TRANSFORM_FRAMES = {6, 7, 8, 9, 10};

    private int fading;

    /**
     * Creates a grass actor
     */
    public Grass()
    {
        fading = -1;
        setImageFrame(DEFAULT_IMAGE);
    }

    /**
     * Overrides dying so that it doesn't die from anything,
     * as this is a fixture.
     *
     * @param attacker the component who is attacking.
     * @return false always since it doesn't die.
     */
    @Override
    public boolean die(Component attacker)
    {
        return false;
    }

    /**
     * Interacts with other actors, always allowing other actors pass.
     *
     * @param other other actor to interact with.
     * @return always returns true to allow anything to pass through it.
     */
    @Override
    public boolean interact(Actor other)
    {
        return true;
    }

    /**
     * Checks whether an interaction with another actor is possible.
     * This will always let actors pass through
     *
     * @param other the other actor to interact with.
     * @return true to always allow others to enter.
     */
    public boolean checkInteract(Actor other)
    {
        return true;
    }

    /**
     * Obtains the dominance level of grass. Grass should be at the top of
     * the z-order, therefore in such a case, when calculating z-order,
     * the grass will return an artificially low dominance level. However,
     * in other cases, it will have high dominance level of a FIXTURE, so
     * to not interfere with others.
     *
     * @param other the other actor to compare with.
     * @return dominance level of a mover.
     */
    @Override
    public DominanceLevel dominanceLevelFor(Actor other)
    {
        if (other == null)
            return TOP;
        else
            return FIXTURE;
    }

    /**
     * Overrides each frame update so that it will fade away if player is
     * within one cardinal direction of grass.
     *
     * @param frame the current frame number
     */
    @Override
    public void updateFrame(long frame)
    {
        super.updateFrame(frame);

        Grid g = getParentGrid();
        if (g == null)
            return;

        if (fading >= 0)
        {
            fading++;
            if (fading >= TRANSFORM_FRAMES.length * DEF_ANIMATION_FRAME_CHANGE)
                g.removeActor(this);
        } else
        {
            if (g.getPlayer() == null)
                return;

            Location loc = getHeadLocation();
            Location playerLoc = g.getPlayer().getHeadLocation();

            int dr = Math.abs(loc.getRow() - playerLoc.getRow());
            int dc = Math.abs(loc.getColumn() - playerLoc.getColumn());
            if (dr + dc <= 1)
            {
                fading = 0;
                animateFrames(TRANSFORM_FRAMES, false);
            }
        }
    }
}
