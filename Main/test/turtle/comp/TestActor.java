package turtle.comp;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import turtle.core.Actor;
import turtle.core.Component;
import turtle.core.DominanceLevel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This is a configurable actor that is used for test cases.
 *
 * @author Henry Wang
 */
public class TestActor extends Actor {
    private static final long serialVersionUID = 2331309440051226520L;
    public static final int COLOR_PARAMS = 4;
    private transient Color back;
    private int level;
    private boolean killer;
    private boolean wall;

    /**
     * @return the background color
     */
    public Color getBack() {
        return back;
    }

    /**
     * @param back new background color
     */
    public void setBack(Color back) {
        setBackground(new Background(new BackgroundFill(back, null, null)));
        this.back = back;
    }

    /**
     * @return the current dominance level
     */
    public int getDomLevel() {
        return level;
    }

    /**
     * @param level new dominance level number
     */
    public void setDomLevel(int level) {
        this.level = level;
    }

    /**
     * @return true if component kills others, false if it doesn't
     */
    public boolean isKiller() {
        return killer;
    }

    /**
     * @param killer true if component kills others, false if it doesn't
     */
    public void setKiller(boolean killer) {
        this.killer = killer;
    }

    /**
     * @return true if actor is wall-like, false if it isn't
     */
    public boolean isWall() {
        return wall;
    }

    /**
     * @param wall true if actor is wall-like, false if it isn't
     */
    public void setWall(boolean wall) {
        this.wall = wall;
    }

    /**
     * Checks if this actor can interact with other actor. Does not execute
     * any actions (i.e. killing).
     *
     * @param other the other actor to interact with.
     * @return true to allow interact, false if not allowed
     */
    @Override
    public boolean checkInteract(Actor other) {
        return !wall;
    }

    @Override
    public boolean die(Component attacker) {
        System.out.println(getLogHeading() + ": I died from " + attacker);
        return super.die(attacker);
    }

    /**
     * Interacts with other actor. Logs the interaction that occurred.
     *
     * @param other the other actor to interact with.
     * @return true to allow interact, false if not allowed
     */
    @Override
    public boolean interact(Actor other) {
        System.out.println(getLogHeading() + ": " + other +
                " interacted with me!");
        if (!wall && killer) {
            other.die(this);
        }
        return !wall;
    }

    /**
     * @param other other actor to compare with (or null for generally).
     * @return a dominance level preset by the parameters
     */
    @Override
    public DominanceLevel dominanceLevelFor(Actor other) {
        return new DominanceLevel("" + level, level);
    }

    /**
     * @return heading string describing test-actor.
     */
    private String getLogHeading() {
        return "@" + getHeadLocation() + "D" + level;
    }

    /**
     * Reads this object from the provided input stream.
     *
     * @param in the input stream to read from
     * @throws IOException            if an I/O error occurs
     * @throws ClassNotFoundException if a class cannot be found.
     */
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        Object data = in.readObject();
        double[] color;
        if (!(data instanceof double[]) || (color = (double[]) data).length !=
                COLOR_PARAMS) {
            throw new IOException("Unable to read background color");
        }
        back = new Color(color[3], color[0], color[1], color[2]);
    }

    /**
     * Writes this object to the provided output stream.
     *
     * @param out the output stream to read to
     * @throws IOException if an I/O error occurs
     */
    private void writeObject(ObjectOutputStream out)
            throws IOException {
        out.defaultWriteObject();
        out.writeObject(new double[]{back.getOpacity(), back.getRed(), back
                .getGreen(), back.getBlue()});
    }
}
