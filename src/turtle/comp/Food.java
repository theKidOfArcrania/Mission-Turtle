package turtle.comp;

import turtle.core.Actor;

/**
 * Food.java
 * <p>
 * This item can be collected by player to help fulfill one food requirement.
 * Player must collect the specified number of these items
 *
 * @author Henry
 */
public class Food extends Item
{
    /**
     * The default image for this component
     */
    public static final int DEFAULT_IMAGE = 54;
    private static final int FOOD_IMAGE = 54;

    /**
     * Constructs a new food item and initializes image.
     */
    public Food()
    {
        setImageFrame(FOOD_IMAGE);
    }

    /**
     * Checks whether an interaction with another actor is possible.
     * Overrides so to only allow players to pass this item.
     *
     * @param other the other actor to interact with.
     * @return true to allow passing, false to deny passing.
     */
    public boolean checkInteract(Actor other)
    {
        return other instanceof Player;
    }

    /**
     * Overrides the interact method to say that only players may
     * pass this item.
     *
     * @param other the actor to interact with.
     * @return true to allow passing, false to deny passing.
     */
    @Override
    public boolean interact(Actor other)
    {
        return other instanceof Player && super.interact(other);
    }

    /**
     * Checks whether if two items are identical in specs.
     *
     * @param other the other item to compare with.
     * @return true if two items are identical, false if not.
     */
    @Override
    public boolean identical(Item other)
    {
        return other instanceof Food;
    }

    /**
     * @return the food image index, representing as an item.
     */
    @Override
    public int getItemImage()
    {
        return FOOD_IMAGE;
    }

}
