package turtle.comp;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import turtle.core.Actor;
import turtle.core.Component;
import turtle.core.DominanceLevel;

import java.util.Map;

/**
 * turtle.comp.TestActor.java
 * <p>
 * This is a configurable actor that is used for test cases.
 *
 * @author Henry Wang
 *         Date: 05/16/2017
 *         Period: 2
 */
public class TestActor extends Actor
{
    private Color back;
    private int level;
    private boolean killer;
    private boolean wall;

    /**
     * @return the background color
     */
    public Color getBack()
    {
        return back;
    }

    /**
     * @param back new background color
     */
    public void setBack(Color back)
    {
        setBackground(new Background(new BackgroundFill(back, null, null)));
        this.back = back;
    }

    /**
     * @return the current dominance level
     */
    public int getDomLevel()
    {
        return level;
    }

    /**
     * @param level new dominance level number
     */
    public void setDomLevel(int level)
    {
        this.level = level;
    }

    /**
     * @return true if component kills others, false if it doesn't
     */
    public boolean isKiller()
    {
        return killer;
    }

    /**
     * @param killer true if component kills others, false if it doesn't
     */
    public void setKiller(boolean killer)
    {
        this.killer = killer;
    }

    /**
     * @return true if actor is wall-like, false if it isn't
     */
    public boolean isWall()
    {
        return wall;
    }

    /**
     * @param wall true if actor is wall-like, false if it isn't
     */
    public void setWall(boolean wall)
    {
        this.wall = wall;
    }

    /**
     * Sets a series of parameters for this test actor. This
     * below specifies the list of parameters:
     * <table>
     * <tr>
     * <th>Name</th>
     * <th>Type</th>
     * <th>Description</th>
     * </tr>
     * <tr>
     * <td><code>back</code></td>
     * <td><code>Color</code></td>
     * <td>Background color of actor</td>
     * </tr>
     * <tr>
     * <td><code>level</code></td>
     * <td><code>int</code></td>
     * <td>Dominance level of actor</td>
     * </tr>
     * <tr>
     * <td><code>killer</code></td>
     * <td><code>boolean</code></td>
     * <td>Determines whether if actor will kill others</td>
     * </tr>
     * <tr>
     * <td><code>wall</code></td>
     * <td><code>boolean</code></td>
     * <td>Determines whether if actor acts wall-like</td>
     * </tr>
     * </table>
     * Note: unlike other components, this does not check for type-safe on
     * parameters, since this is only a test component and not used for
     * production.
     *
     * @param params the parameter object.
     */
    @Override
    public void setParameters(Map<String, Object> params)
    {
        super.setParameters(params);
        if (params.containsKey("back"))
            setBack((Color) params.get("back"));
        if (params.containsKey("level"))
            setDomLevel((Integer) params.get("level"));
        if (params.containsKey("killer"))
            setKiller((Boolean) params.get("killer"));
        if (params.containsKey("wall"))
            setWall((Boolean) params.get("wall"));
    }

    /**
     * Checks if this actor can interact with other actor. Does not execute
     * any actions (i.e. killing).
     *
     * @param other the other actor to interact with.
     * @return true to allow interact, false if not allowed
     */
    @Override
    public boolean checkInteract(Actor other)
    {
        return !wall;
    }

    @Override
    public boolean die(Component attacker)
    {
        System.out.println(getLogHeading() + ": I died from " + attacker);
        return super.die(attacker);
    }

    /**
     * Interacts with other actor. Logs the interaction that occured.
     *
     * @param other the other actor to interact with.
     * @return true to allow interact, false if not allowed
     */
    @Override
    public boolean interact(Actor other)
    {
        System.out.println(getLogHeading() + ": " + other +
                " interacted with me!");
        if (!wall && killer)
            other.die(this);
        return !wall;
    }

    /**
     * @param other other actor to compare with (or null for generally).
     * @return a dominance level preset by the parameters
     */
    @Override
    public DominanceLevel dominanceLevelFor(Actor other)
    {
        return new DominanceLevel("" + level, level);
    }

    /**
     * @return heading string describing test-actor.
     */
    private String getLogHeading()
    {
        return "@" + getHeadLocation() + "D" + level;
    }
}
