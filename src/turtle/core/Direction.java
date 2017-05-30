package turtle.core;

/**
 * Direction.java
 *
 * @author Henry Wang
 */
public enum Direction
{
    NORTH(-1, 0), EAST(0, 1), SOUTH(1, 0), WEST(0, -1);

    private int rowIncrement;
    private int colIncrement;

    /**
     * Constructs a Direction
     * @param rowIncrement increment in row
     * @param colIncrement increment in column
     */
    Direction(int rowIncrement, int colIncrement)
    {
        this.rowIncrement = rowIncrement;
        this.colIncrement = colIncrement;
    }

    /**
     * Generates a random direction.
     *
     * @return the random direction
     */
    public static Direction randomDirection()
    {
        Direction[] dirs = Direction.values();
        return dirs[(int)(Math.random() * dirs.length)];
    }

    /**
     * Moves direction num turns clockwise and obtains the resulting direction
     * @param num the number of clockwise turns (can be negative)
     * @return the resulting direction after turns.
     */
    public Direction turn(int num)
    {
        Direction[] dirs = Direction.values();
        return dirs[(ordinal() + num) % dirs.length];
    }

    /**
     * Traverses this location position in this direction.
     * @param pos the initial position
     */
    public void traverse(Location pos)
    {
        pos.setLocation(pos.getRow() + rowIncrement, pos.getColumn() + colIncrement);
    }
}
