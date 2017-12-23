package turtle.core;

import turtle.attributes.NotAttribute;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Represents a movable/ interactive grid component that is readily mutable.
 * The biggest difference between an actor and a cell is that actors are transparent
 * having an ability to show through the bottom cell layer.
 *
 * @author Henry Wang
 */
public abstract class Actor extends Component {
    public static final int DYING_FRAMES = 10;

    /**
     * Angle measurement for right angle
     */
    public static final int RIGHT_ANGLE = 90;

    //Some common dominance levels.
    public static final DominanceLevel TOP = new DominanceLevel("Top", -200);
    public static final DominanceLevel FLOATING = new DominanceLevel("Floating", -100);
    public static final DominanceLevel PLAYER = new DominanceLevel("Player", 0);
    public static final DominanceLevel ENEMY = new DominanceLevel("Enemy", 100);
    public static final DominanceLevel MOVER = new DominanceLevel("Mover", 200);
    public static final DominanceLevel ITEM = new DominanceLevel("Item", 300);
    public static final DominanceLevel FIXTURE = new DominanceLevel("Fixture", 400);

    private static final long serialVersionUID = -8229684437846026366L;

    private boolean dying;
    private boolean dead;
    private int dieFrame;
    private Direction heading;

    /**
     * Constructs a new actor.
     */
    public Actor() {
        dying = false;
        heading = Direction.NORTH;
    }

    /**
     * Kills this actor (this sets a flag for this actor to be removed).
     * Any class can override this method to determine which items this
     * actor will die by (whether if it is immune to something).
     *
     * @param attacker the thing that is killing this actor.
     * @return true if this actor died as a result of this call, false if
     * nothing changed.
     */
    public boolean die(Component attacker) {
        if (isDying()) {
            return false;
        }
        if (attacker instanceof Actor && ((Actor) attacker).isDying()) {
            return false;
        }

        dying = true;
        dieFrame = 0;
        return true;
    }

    /**
     * @return the current direction heading
     */
    public Direction getHeading() {
        return heading;
    }

    /**
     * This sets the direction the actor is facing, and also rotates the
     * actor to that direction.
     *
     * @param heading the new direction heading to set
     */
    public void setHeading(Direction heading) {
        setRotate(heading.ordinal() * RIGHT_ANGLE);
        this.heading = heading;
    }

    /**
     * Checks whether if this actor has been killed and is dying.
     *
     * @return true if died, false if alive.
     */
    @NotAttribute
    public boolean isDying() {
        return dying;
    }

    /**
     * Checks whether if this actor is dead and marked for removal.
     *
     * @return true if dead, false if dying or alive.
     */
    @NotAttribute
    public boolean isDead() {
        return dead;
    }

    /**
     * Checks whether an interaction with another actor is possible.
     * This should suppress any actions done in the {@link #interact(Actor)}
     * method.
     *
     * @param other the other actor to interact with.
     * @return true if the other actor can pass into location
     * false if other actor is prohibited to pass.
     */
    public abstract boolean checkInteract(Actor other);

    /**
     * Executes an interaction with another actor.
     *
     * @param other the other actor to interact with.
     * @return true if the other actor can pass into location
     * false if other actor is prohibited to pass.
     */
    public abstract boolean interact(Actor other);

    /**
     * Obtains the dominance level of the actor in relation to another actor.
     *
     * @param other other actor to compare with (or null for generally).
     * @return a dominance level of the actor.
     */
    public abstract DominanceLevel dominanceLevelFor(Actor other);

    /**
     * Executes move for an actor in a specified direction. Convenience
     * method for {@link #traverseDirection(Direction, boolean)}.
     *
     * @param direction direction to move in.
     * @return true if successful, false otherwise.
     */
    public boolean traverseDirection(Direction direction) {
        return traverseDirection(direction, true);
    }

    /**
     * Checks or executes move for an actor in a specified direction.
     *
     * @param direction direction to move in.
     * @param execute   determine whether to execute move or just check move
     * @return true if successful, false otherwise.
     */
    public boolean traverseDirection(Direction direction, boolean execute) {
        if (isDying()) {
            return false;
        }

        Grid parent = getParentGrid();
        if (parent == null) {
            return false;
        }

        Location loc = new Location(getHeadLocation());
        direction.traverse(loc);

        if (execute) {
            return parent.moveActor(this, loc.getRow(), loc.getColumn());
        } else {
            return parent.checkMove(this, loc.getRow(), loc.getColumn());
        }
    }

    /**
     * Updates frames of actor. This particularly updates dying frames.
     *
     * @param frame the frame number.
     */
    @Override
    public void updateFrame(long frame) {
        super.updateFrame(frame);
        if (dying) {
            if (dyingFrame(dieFrame)) {
                dead = true;
            }
            dieFrame++;
        }

    }

    /**
     * Handles the dying frames. By default this makes the actor lighter and
     * lighter until it disappears.
     *
     * @param dieFrame the current dying frame number.
     * @return true if this actor is now "dead", false if it is still dying.
     */
    protected boolean dyingFrame(long dieFrame) {
        setOpacity(1 - ((double) dieFrame / DYING_FRAMES));
        return dieFrame >= DYING_FRAMES;
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
        setHeading(getHeading());
    }
}
