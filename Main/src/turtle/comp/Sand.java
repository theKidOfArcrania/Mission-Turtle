package turtle.comp;

import turtle.core.Actor;
import turtle.core.Cell;

/**
 * Represents the basic land entity.
 *
 * @author Henry Wang
 */
public class Sand extends Cell {
    public static final int DEFAULT_IMAGE = 26;
    private static final long serialVersionUID = 2418338281295706831L;

    /**
     * Executes a pass when an visitor actor comes to this cell. This
     * lets everything to pass this sand.
     *
     * @param visitor the actor passing this cell
     * @return true always to permit such a move.
     */
    @Override
    public boolean pass(Actor visitor) {
        return true;
    }

    /**
     * Checks whether if a pass to this cell is ever possible. This
     * lets everything to pass this sand.
     *
     * @param visitor the visitor to check against.
     * @return true always to permit such a move.
     */
    @Override
    public boolean checkPass(Actor visitor) {
        return true;
    }

}
