package turtle.comp;

/**
 * This item can be collected by the player in order to unlock a door
 * of the same color.
 *
 * @author Henry Wang
 */
public class Key extends Item {
    /**
     * The default image for this component
     */
    public static final int DEFAULT_IMAGE = 45;
    private static final int KEY_OFFSET_IMAGE = 45;
    private static final long serialVersionUID = 764295184211414467L;

    private ColorType color;

    /**
     * Constructs a new Key by initializing UI. It will
     * by default initialize to the color RED.
     */
    public Key() {
        setColor(ColorType.YELLOW);
    }

    /**
     * @return the color of this key
     */
    public ColorType getColor() {
        return color;
    }

    /**
     * @param color the new color to set for this key
     * @throws NullPointerException if the color supplied is null.
     */
    public void setColor(ColorType color) {
        if (color == null) {
            throw new NullPointerException();
        }
        setImageFrame(color.getImageFrame(KEY_OFFSET_IMAGE));
        this.color = color;
    }

    /**
     * Checks whether if this item is identical as another. This will
     * say true if other item is key and has same color.
     *
     * @param other other item to compare with
     * @return true if both items are identical
     */
    @Override
    public boolean identical(Item other) {
        return other instanceof Key && ((Key) other).getColor() == color;
    }

    /**
     * Obtains the index that should be displayed as item
     *
     * @return index of image frame in tileset
     */
    @Override
    public int getItemImage() {
        return color.getImageFrame(KEY_OFFSET_IMAGE);
    }

}
