package turtle.comp;

import javafx.scene.image.ImageView;
import turtle.core.*;

import java.io.IOException;
import java.lang.reflect.Field;

import static turtle.core.Actor.*;

/**
 * Factory.java
 * <p>
 * This actor will attempt to clone a particular component over and over in its
 * facing direction only when the linked button(s) is triggered.
 *
 * @author Henry
 *         Date: 5/9/17
 *         Period: 2
 */
public class Factory extends Cell
{
    /**
     * The default image for this component
     */
    public static final int DEFAULT_IMAGE = 70;
    private static final int FACTORY_OFFSET_IMAGE = DEFAULT_IMAGE;
    private static final double RATIO_CLONE_IMG = .8;
    private static final long serialVersionUID = -8544196441607182765L;

    private boolean headingMatters = false;
    private Direction heading;
    private short componentCloned;
    private long currentFrame;
    private long cloning;

    private ColorType color;

    private transient ImageView clonedImg;

    /**
     * Constructs a new factory.
     */
    public Factory()
    {
        heading = Direction.NORTH;
        headingMatters = false;
        cloning = -1;
        currentFrame = 0;
        componentCloned = -1;

        initCloneImg();
        setColor(ColorType.YELLOW);
    }

    /**
     * Initializes the clone image-view UI.
     */
    private void initCloneImg()
    {
        TileSet ts = getTileSet();
        double size = ts.getFrameSize() * RATIO_CLONE_IMG;

        clonedImg = new ImageView();
        clonedImg.setFitHeight(size);
        clonedImg.setFitWidth(size);
        clonedImg.setImage(ts.getImageSet());
        clonedImg.setViewport(ts.frameAt(-1));
        this.getChildren().add(clonedImg);
    }

    /**
     * @return the color of this factory
     */
    public ColorType getColor()
    {
        return color;
    }

    /**
     * @param color the new color to set for this factory
     * @throws NullPointerException if the color supplied is null.
     */
    public void setColor(ColorType color)
    {
        if (color == null)
            throw new NullPointerException();
        setImageFrame(color.getImageFrame(FACTORY_OFFSET_IMAGE));
        this.color = color;
    }

    /**
     * @return the current component id that is being cloned.
     */
    public short getCloned()
    {
        return componentCloned;
    }

    /**
     * Changes the current component being cloned.
     *
     * @param componentCloned the component id to clone
     * @throws IllegalArgumentException if the component id is not a valid
     *                                  actor.
     */
    public void setCloned(short componentCloned)
    {
        Class<Component> comp = getTileSet().componentAt(componentCloned);
        if (!Actor.class.isAssignableFrom(comp))
            throw new IllegalArgumentException("Component is not an actor");

        this.componentCloned = componentCloned;
        headingMatters = testHeadingMatters(comp);
        setHeading(heading);

        try
        {
            Field img = comp.getDeclaredField("DEFAULT_IMAGE");
            img.setAccessible(true);
            clonedImg.setViewport(getTileSet().frameAt(img.getInt(null)));
        }
        catch (NoSuchFieldException | IllegalArgumentException |
                IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Clones one actor in that particular direction. This will wait to clone
     * next animation frame, so to prevent a stack-overflow of clones.
     */
    public void cloneActor()
    {
        cloning = currentFrame;
    }

    /**
     * @return the current direction heading
     */
    public Direction getHeading()
    {
        return heading;
    }

    /**
     * This sets the direction the factory is facing, and also rotates the
     * factory to that direction. This also determines the direction the
     * factory will clone its actors.
     *
     * @param heading the new direction heading to set
     */
    public void setHeading(Direction heading)
    {
        if (!headingMatters)
            clonedImg.setRotate(-heading.ordinal() * RIGHT_ANGLE);
        else
            clonedImg.setRotate(0);
        setRotate(heading.ordinal() * RIGHT_ANGLE);
        this.heading = heading;
    }

    /**
     * Checks if an actor can pass this location. This never lets anything
     * pass through.
     *
     * @param visitor actor visiting the factory.
     * @return false always to deny passing.
     */
    @Override
    public boolean checkPass(Actor visitor)
    {
        return false;
    }

    /**
     * Checks if an actor can pass this location. This never lets anything
     * pass through.
     *
     * @param visitor actor visiting the factory.
     * @return false always to deny passing.
     */
    @Override
    public boolean pass(Actor visitor)
    {
        return false;
    }

    /**
     * Updates current animation frame. This overrides the update frame
     * so that we can do the cloning (one frame delayed).
     *
     * @param frame the current frame number.
     */
    @Override
    public void updateFrame(long frame)
    {
        super.updateFrame(frame);
        currentFrame = frame;
        if (cloning >= 0)
            doClone();
    }

    /**
     * Does the actual cloning. Waits for at least one frame before
     * doing the actual cloning.
     */
    private void doClone()
    {
        Grid parent = getParentGrid();
        if (parent == null || componentCloned == -1)
            return;

        if (cloning < 0 || cloning >= currentFrame)
            return;

        cloning = -1;

        Location loc = new Location(getHeadLocation());
        heading.traverse(loc);

        try
        {
            Class<Component> comp = getTileSet().componentAt(componentCloned);
            Actor clone = (Actor) comp.newInstance();
            clone.setHeading(heading);
            clone.getHeadLocation().setLocation(loc);
            clone.getTrailingLocation().setLocation(loc);
            parent.placeActor(clone);
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Tests whether if heading matters with a particular actor.
     * Assumes that this class is an actor.
     *
     * @param actor the actor class to to test for the heading.
     * @return true if it matters, false if it doesn't.
     */
    private boolean testHeadingMatters(Class<Component> actor)
    {
        Actor a;
        try
        {
            a = (Actor) actor.newInstance();
            Direction before = a.getHeading();
            if (before == Direction.NORTH)
                a.setHeading(Direction.EAST);
            else
                a.setHeading(Direction.NORTH);
            return a.getHeading() != before;
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reads this object from the provided input stream.
     *
     * @param in the input stream to read from
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if a class cannot be found.
     */
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        initCloneImg();
        setCloned(getCloned());
        setHeading(getHeading());
    }
}
