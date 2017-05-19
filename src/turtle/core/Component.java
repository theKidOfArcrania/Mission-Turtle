package turtle.core;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import turtle.attributes.Attributable;
import turtle.attributes.AttributeSet;

import java.util.Map;
import java.util.Random;

/**
 * Component.java
 * <p>
 * Represents the abstract base of all grid components
 * that will be displayed on the Grid.
 *
 * @author Henry Wang
 *         Date: 4/26/17
 *         Period: 2
 */
public abstract class Component extends Pane implements Attributable
{

    /**
     * Utility method used to shuffle an array.
     *
     * @param arr the array to shuffle
     * @param rng the random generator to shuffle with.
     */
    public static void shuffle(int[] arr, Random rng)
    {
        for (int i = 0; i < SHUFFLE * arr.length; i++)
        {
            int a = rng.nextInt(arr.length);
            int b = rng.nextInt(arr.length);
            int tmp = arr[a];
            arr[a] = arr[b];
            arr[b] = tmp;
        }
    }

    /**
     * The default number of frames to wait to change one image in an
     * animation sequence.
     */
    public static final int DEF_ANIMATION_FRAME_CHANGE = 4;

    /**
     * The default tile-set to use.
     */
    public static final TileSet DEFAULT_SET = new TileSet();

    /**
     * The number of frames equating to a big frame
     * (defined as the player moving one frame.
     */
    public static final int BIG_FRAME = 10;

    private static final int SHUFFLE = 50;
    private final TileSet ts;
    private final ImageView img;
    private final Location headLoc;
    private final Location trailLoc;
    private final AttributeSet<Component> attributes;

    private Grid parent;
    private long curFrame;
    private int currentImage;
    private long animationOffset;
    private int[] imageFrames;
    private int changeRate;
    private boolean animationCycle;


    /**
     * Constructs a new component with the image background.
     */
    protected Component()
    {
        ts = DEFAULT_SET;

        img = new ImageView(ts.getImageset());
        this.getChildren().add(img);

        headLoc = new Location();
        trailLoc = new Location();

        setImageFrame(-1);
        curFrame = 0;

        attributes = new AttributeSet<>(this);
    }

    /**
     * Animates component through a series of frames at the default
     * change rate (change once per 4 frames).
     *
     * @param imageFrames    the animation frames to animate through.
     * @param animationCycle true to cycle through frames, false to run once.
     * @throws IndexOutOfBoundsException if any of the frame indexes are
     *                                   out of bounds.
     * @throws IllegalArgumentException  if the changeRate is not positive.
     * @see #animateFrames(int[], boolean, int)
     */
    public void animateFrames(int[] imageFrames, boolean animationCycle)
    {
        animateFrames(imageFrames, animationCycle, DEF_ANIMATION_FRAME_CHANGE);
    }

    /**
     * Sets this component to animate through the frames indexes at a
     * specified change rate.
     *
     * @param imageFrames    the image frames to animate through.
     * @param animationCycle true to cycle through frames, false to run once.
     * @param changeRate     the number of frames to wait between frames.
     * @throws IndexOutOfBoundsException if any of the frame indexes are
     *                                   out of bounds.
     * @throws IllegalArgumentException  if the changeRate is not positive.
     */
    public void animateFrames(int[] imageFrames, boolean animationCycle,
                              int changeRate)
    {
        //Test for index out of bounds.
        for (int f : imageFrames)
            ts.frameAt(f);

        if (changeRate <= 0)
            throw new IllegalArgumentException("changeRate must be positive.");

        animationOffset = curFrame;
        this.animationCycle = animationCycle;
        this.imageFrames = imageFrames;
        this.changeRate = changeRate;

        setViewport(imageFrames[0]);
    }

    @Override
    public AttributeSet<Component> getAttributeSet()
    {
        return attributes;
    }

    /**
     * @return the head location of the actor
     */
    public Location getHeadLocation()
    {
        return headLoc;
    }

    /**
     * Obtains the move speed of this component.
     *
     * @return move speed in pixels per frame
     */
    public double getMoveSpeed()
    {
        return getTileSet().getFrameSize() / BIG_FRAME;
    }

    /**
     * @return the parent grid that contains this component
     * or null if there is no parent.
     */
    public Grid getParentGrid()
    {
        return parent;
    }

    /**
     * Sets a new parent grid. This should only be called internally
     * by Grid when this is added.
     *
     * @param parent the new parent grid to set to
     */
    protected void setParentGrid(Grid parent)
    {
        this.parent = parent;
    }

    /**
     * @return the current tileset used by component
     */
    public TileSet getTileSet()
    {
        return ts;
    }

    /**
     * @return the trailing location of the actor
     */
    public Location getTrailingLocation()
    {
        return trailLoc;
    }

    /**
     * Obtains next step of incrementing a value
     *
     * @param from intial value
     * @param to   the goal value to achieve
     * @param step the increment value per step
     * @return the next value to increment to.
     * @throws IllegalArgumentException if step is zero.
     */
    private double increment(double from, double to, double step)
    {
        if (step == 0)
            throw new IllegalArgumentException("Step must be non-zero.");
        if (from == to)
            return to;
        if (from < to ^ step > 0)
            return increment(from, to, -step);

        double after = from + step;
        if (after > to ^ from > to)
            // Incremented pass the target.
            return to;
        else
            return after;
    }

    /**
     * Determines whether if this component is moving.
     *
     * @return true if moving, false if it is still
     */
    public boolean isMoving()
    {
        return !headLoc.equals(trailLoc);
    }

    /**
     * Layouts all nodes in the center by default, spanning full size.
     */
    @Override
    protected void layoutChildren()
    {
        for (Node n : getManagedChildren())
            layoutInArea(n, 0, 0, getWidth(), getHeight(), 0, HPos.CENTER,
                    VPos.CENTER);
    }

    /**
     * Moves this component one frame step in the direction it is moving if any.
     */
    private void move()
    {
        if (parent != null)
        {
            boolean validLocs = headLoc.isValidLocation() &&
                    trailLoc.isValidLocation();
            if (validLocs)
            {
                double speed = getMoveSpeed();
                int cellSize = parent.getCellSize();
                int xPos = cellSize * headLoc.getColumn();
                int yPos = cellSize * headLoc.getRow();
                if (xPos != getTranslateX())
                    setTranslateX(increment(getTranslateX(), xPos, speed));
                if (yPos != getTranslateY())
                    setTranslateY(increment(getTranslateY(), yPos, speed));

                if (xPos == getTranslateX() && yPos == getTranslateY())
                    trailLoc.setLocation(headLoc);
            }
        }
    }

    /**
     * Sets the image of this component to the given index and disables
     * any current animations.
     *
     * @param index the index from the TileSet of frames.
     * @throws IndexOutOfBoundsException if image frame index is out of bounds.
     */
    public void setImageFrame(int index)
    {
        setViewport(index);
        animationOffset = -1;
        imageFrames = null;
        changeRate = -1;
    }

    /**
     * Internally sets the current image viewport to a specific frame.
     * This will avoid setting the viewport to the same index twice in a row
     * for performance reasons.
     *
     * @param index the index of image frame
     */
    private void setViewport(int index)
    {
        if (currentImage == index)
            return;
        img.setViewport(ts.frameAt(index));
        currentImage = index;
    }

    /**
     * Updates the current image to step forward one frame if it is in a sequence
     * of animation.
     *
     * @param frame the current frame number.
     */
    private void updateAnimation(long frame)
    {
        if (animationOffset != -1 && (frame - animationOffset) % changeRate == 0)
        {
            int stepInd = (int) ((frame - animationOffset) / changeRate);
            if (animationCycle)
            {
                stepInd %= imageFrames.length;
                setViewport(imageFrames[stepInd]);
            } else
            {
                if (stepInd >= imageFrames.length - 1)
                    setImageFrame(imageFrames[imageFrames.length - 1]);
                else
                    setViewport(imageFrames[stepInd]);
            }
        }
    }

    /**
     * Updates a frame of animation for a Component.
     * Note: Subclasses should ALWAYS call <code>super.updateFrame()</code>
     * in order that all the super classes get updated as well.
     *
     * @param frame current frame number
     */
    public void updateFrame(long frame)
    {
        curFrame = frame;
        updateAnimation(frame);
        move();
    }
}
