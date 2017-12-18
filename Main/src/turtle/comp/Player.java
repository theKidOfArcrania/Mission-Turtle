package turtle.comp;

import javafx.scene.transform.Rotate;
import turtle.core.Actor;
import turtle.core.Component;
import turtle.core.DominanceLevel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represents the player unit within the game. The player would directly
 * control this character within the game.
 *
 * @author Henry Wang
 */
public class Player extends Actor {
    public static final int DEFAULT_IMAGE = 40;

    private static final double ITEM_RADIUS = .65;
    private static final double SEMI_TRANSPARENT = .5;

    private static final int FRAME_STILL = DEFAULT_IMAGE;
    private static final int[] FRAME_ANIMATE = {41, 42, 43, DEFAULT_IMAGE};

    private static final long serialVersionUID = -422294616581707080L;

    private final ArrayList<Item> pocket;
    private final ArrayList<ItemSlot> slots;

    private Component msgSender;
    private String msg;

    private boolean winner;
    private boolean moving;
    @SuppressWarnings("CanBeFinal")
    private double itemOpacity;

    /**
     * Constructs a new player.
     */
    public Player() {
        winner = false;
        moving = false;
        pocket = new ArrayList<>();
        slots = new ArrayList<>();

        itemOpacity = SEMI_TRANSPARENT;
        initItemHover();
    }

    /**
     * Checks whether an interaction with another actor is possible.
     * This will always let actors pass through
     *
     * @param other the other actor to interact with.
     * @return true to always allow others to enter.
     */
    public boolean checkInteract(Actor other) {
        return true;
    }

    /**
     * Adds a new item to the player. This will currently only accept
     * keys.
     *
     * @param itm the item to add.
     * @return true if this item is collected, false if it is not.
     */
    public boolean collectItem(Item itm) {
        if (getParentGrid() == null) {
            return false;
        }

        if (itm instanceof Food) {
            getParentGrid().incrementFood();
        }

        pocket.add(itm);
        for (ItemSlot slot : slots) {
            if (slot.addItem(itm)) {
                return true;
            }
        }

        ItemSlot newSlot = new ItemSlot();
        newSlot.addItem(itm);
        initItemSlot(newSlot);

        slots.add(newSlot);
        getChildren().add(newSlot);

        layoutSlots();
        return true;
    }

    /**
     * Kills this actor (this sets a flag for this actor to be removed).
     * This overrides it to be immune to water.
     *
     * @param attacker the thing that is killing this actor.
     * @return true if this actor died as a result of this call, false if
     * nothing changed.
     */
    @Override
    public boolean die(Component attacker) {
        return !(attacker instanceof Water) && super.die(attacker);
    }

    /**
     * Obtains the dominance level of this player. It will always
     * have the lowest dominance level of all characters.
     *
     * @param other the other actor to compare with.
     * @return dominance level of player.
     */
    @Override
    public DominanceLevel dominanceLevelFor(Actor other) {
        return PLAYER;
    }

    /**
     * @return the message that the player should see now. Never returns null.
     * @see #setMessage(String, Component)
     */
    public String getMessage() {
        if (msg == null) {
            return "";
        }
        return msg;
    }

    /**
     * @return a read-only list of items the user has stored.
     */
    public List<Item> getPocket() {
        return Collections.unmodifiableList(pocket);
    }

    /**
     * Interacts with other actors. This does nothing since every actor
     * should dominate over player.
     *
     * @param other other actor to compare with.
     * @return always true.
     */
    @Override
    public boolean interact(Actor other) {
        return true;
    }

    /**
     * Determines whether if player won the game.
     *
     * @return true if player won, false if game is still running.
     */
    public boolean isWinner() {
        return winner;
    }

    /**
     * Resets message if the sender sent this message (i.e. it has not
     * already been overridden by someone else.
     *
     * @param sender the component that sent the message
     */
    public void resetMessage(Component sender) {
        if (msgSender == sender) {
            this.msg = null;
        }
    }

    /**
     * This is set as a flag so that the UI will then be able
     * to go and display this message to the player. This mechanism
     * will allow the user to read some information that might
     * help them along in the level.
     *
     * @param msg    the new message to show the player.
     * @param sender the component sending the message.
     */
    public void setMessage(String msg, Component sender) {
        this.msg = msg;
        this.msgSender = sender;
    }

    /**
     * Updates frame of component. This changes the turtle animations from moving
     * and not moving whenever it is moving or not.
     *
     * @param frame frame number
     */
    @Override
    public void updateFrame(long frame) {
        super.updateFrame(frame);
        if (moving ^ isMoving()) {
            moving = !moving;
            if (moving) {
                animateFrames(FRAME_ANIMATE, true);
            } else {
                setImageFrame(FRAME_STILL);
            }
        }

    }

    /**
     * Finds an item and removes the first such match for the player to use.
     *
     * @param usable a function used to identify which item is usable.
     * @return the first item, or null if it cannot be found.
     */
    public Item useItem(Predicate<Item> usable) {
        Item found = null;
        for (Item itm : pocket)
            if (usable.test(itm)) {
                found = itm;
                break;
            }

        if (found != null) {
            pocket.remove(found);

            Iterator<ItemSlot> itr = slots.iterator();
            while (itr.hasNext()) {
                ItemSlot slot = itr.next();
                if (slot.removeItem(found)) {
                    if (slot.isEmpty()) {
                        itr.remove();
                        getChildren().remove(slot);
                        layoutSlots();
                    }
                    break;
                }
            }
        }

        return found;
    }

    /**
     * Flags that the player has won the game.
     */
    public void win() {
        winner = true;
    }

    /**
     * Initializes the hover listener, which will change opacity of item
     * slots if user hovers over player.
     */
    private void initItemHover() {
        hoverProperty().addListener(observable ->
        {
            if (isHover()) {
                itemOpacity = 1;
            } else {
                itemOpacity = SEMI_TRANSPARENT;
            }
            for (ItemSlot slot : slots)
                slot.setOpacity(itemOpacity);
        });
    }

    /**
     * Initializes an item slot and UI stuff.
     *
     * @param slot the slot to initialize
     */
    private void initItemSlot(ItemSlot slot) {
        Rotate negateRotate = new Rotate(0, Rotate.Z_AXIS);
        negateRotate.angleProperty().bind(rotateProperty().negate());
        negateRotate.pivotXProperty().bind(widthProperty().divide(2)
                .subtract(slot.translateXProperty()));
        negateRotate.pivotYProperty().bind(heightProperty().divide(2)
                .subtract(slot.translateYProperty()));
        slot.getTransforms().add(negateRotate);
        slot.setOpacity(itemOpacity);
    }

    /**
     * Layouts all the current player items within a radius circle around the player.
     */
    private void layoutSlots() {
        double radius = getTileSet().getFrameSize() * ITEM_RADIUS;
        double step = 2 * Math.PI / slots.size();
        for (int i = 0; i < slots.size(); i++) {
            ItemSlot slot = slots.get(i);
            slot.setTranslateX(radius * Math.sin(i * step));
            slot.setTranslateY(radius * -Math.cos(i * step));
        }
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
        initItemHover();
        for (ItemSlot slot : slots)
            initItemSlot(slot);
        getChildren().addAll(slots);
        layoutSlots();
    }
}
