package turtle.comp;

import javafx.scene.image.ImageView;
import turtle.core.Actor;
import turtle.core.Cell;
import turtle.core.TileSet;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Water.java
 * <p>
 * This embodies a cell of water, it is the safety zone of the player.
 *
 * @author Henry Wang
 *         Date: 5/6/17
 *         Period: 2
 */
public class Water extends Cell
{
    /**
     * The default image for this component
     */
    public static final int DEFAULT_IMAGE = 12;

    private static final int[] ANIMATION_FRAME = {DEFAULT_IMAGE, 13, 14, 15, 16};
    private static final int[] TRANSFORM_ANIMATION_FRAME = {30, 29, 28, 27, 26};
    private static final int MAX_TRANSFORM = TRANSFORM_ANIMATION_FRAME.length *
            DEF_ANIMATION_FRAME_CHANGE;
    private static final long serialVersionUID = 991189208764206004L;
    private int frameCount;
    private int topFrame;

    private transient ImageView top;

    /**
     * Constructs a Water tile and initializes UI.
     */
    public Water()
    {
        animateFrames(ANIMATION_FRAME, true);
        frameCount = -1;

        topFrame = -1;
        initTopImage();
    }

    /**
     * Kills everything that passes it.
     *
     * @param visitor the actor passing this cell.
     * @return always returns true to allow visitor to pass cell
     */
    @Override
    public boolean pass(Actor visitor)
    {
        visitor.die(this);
        return true;
    }

    /**
     * Transforms this water cell into sand (and animate it).
     */
    public void transformToSand()
    {
        frameCount = 0;
        transformTo(new Sand(), MAX_TRANSFORM);
    }

    /**
     * Overrides the update frame method in order to animate the
     * transforming to sand frames (layered on top of water
     * animation).
     *
     * @param frame the current frame number
     */
    @Override
    public void updateFrame(long frame)
    {
        super.updateFrame(frame);
        if (frameCount != -1)
        {
            if (frameCount >= DEF_ANIMATION_FRAME_CHANGE)
            {
                topFrame = TRANSFORM_ANIMATION_FRAME
                        [frameCount / DEF_ANIMATION_FRAME_CHANGE - 1];
                top.setViewport(getTileSet().frameAt(topFrame));
            }
            if (frameCount < MAX_TRANSFORM)
                frameCount++;
        }
    }

    /**
     * Checks whether if a pass to this cell is ever possible. Water
     * will kill everything but it will first let it pass through.
     *
     * @param visitor the actor passing this cell.
     * @return always returns true to allow visitor to pass cell
     */
    @Override
    public boolean checkPass(Actor visitor)
    {
        return true;
    }

    /**
     * Initializes the top image.
     */
    private void initTopImage()
    {
        top = new ImageView();
        TileSet ts = getTileSet();
        top.setImage(ts.getImageSet());
        top.setViewport(getTileSet().frameAt(topFrame));
        this.getChildren().add(top);
    }

    /**
     * Reads this object from the provided input stream.
     *
     * @param in the input stream to read from
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if a class cannot be found.
     */
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        initTopImage();
    }
}
