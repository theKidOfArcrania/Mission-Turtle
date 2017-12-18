package turtle.comp;

import turtle.core.Direction;

/**
 * Moves back and forth with no reason or rhyme. Kills turtles within the path.
 *
 * @author Henry
 */
public class LawnMower extends Enemy {
    public static final int DEFAULT_IMAGE = 60;
    private static final long serialVersionUID = 3529870400019445102L;

    /**
     * Updates frames so that the lawn-mower will move in its facing direction.
     * If it is blocked by something, it will attempt to bounce back.
     *
     * @param frame the frame number
     */
    @Override
    public void updateFrame(long frame) {
        super.updateFrame(frame);
        if (!isMoving()) {
            final int[] options = {0, 2};
            Direction heading = getHeading();
            for (int turn : options) {
                Direction newDir = heading.turn(turn);
                if (traverseDirection(newDir, true) ||
                        turn == options[options.length - 1]) {
                    setHeading(newDir);
                    break;
                }
            }
        }
    }
}
