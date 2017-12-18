package turtle.comp;

import turtle.core.Actor;
import turtle.core.Cell;

/**
 * This is the spot where the player can exit and advance to next level.
 *
 * @author Henry wang
 */
public class Exit extends Cell {
    public static final int DEFAULT_IMAGE = 17;
    private static final long serialVersionUID = 6760499019619226276L;

    private Player winner;

    /**
     * Executes an actor passing this cell. This will not let any actors
     * other than player from entering. When player has met food requirements,
     * passing cell means the player will win the game.
     *
     * @param visitor the actor to visit cell
     * @return true to allow pass, false to deny pass.
     */
    @Override
    public boolean pass(Actor visitor) {
        if (visitor instanceof Player) {
            if (getParentGrid().getFoodRequirement() <= 0) {
                winner = (Player) visitor;
            }
            return true;
        }
        return false;
    }

    /**
     * Updates each animation frame so that it will set winner flag
     * when player has finished moving into exit.
     *
     * @param frame the frame number.
     */
    @Override
    public void updateFrame(long frame) {
        super.updateFrame(frame);
        if (winner != null && winner.getTrailingLocation().equals(
                getHeadLocation())) {
            winner.win();
        }
    }

    /**
     * Checks whether if actors can pass this cell. This will not let
     * any actors other than player from entering.
     *
     * @param visitor the actor to visit cell
     * @return true to allow pass, false to deny pass.
     */
    @Override
    public boolean checkPass(Actor visitor) {
        return visitor instanceof Player;
    }
}
