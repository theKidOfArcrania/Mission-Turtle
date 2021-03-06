package turtle.core;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import turtle.comp.Player;

import java.io.*;
import java.util.*;

/**
 * Manages and displays all the grid components in the level.
 *
 * @author Henry Wang
 */
public class Grid extends Pane implements Serializable {
    public static final int CELL_SIZE = 100;
    
    private static final long serialVersionUID = 7918941519839716716L;
    private final StatefulRandom rng;

    private final int rows;
    private final int cols;
    private final Cell[][] base;
    private final HashMap<Actor, Location> actorLocs;

    private transient Pane pnlBase;
    private transient Pane pnlStage;

    private Player player;
    private int foodLeft;
    private int timeLeft;
    private Direction lastMove;
    private final Recording recording;
    private boolean playing;


    /**
     * Creates a new grid with the following dimensions
     *
     * @param rows the number of rows
     * @param cols the number of columns
     */
    public Grid(int rows, int cols) {
        rng = new StatefulRandom();
        recording = new Recording();

        this.rows = rows;
        this.cols = cols;
        foodLeft = 0;
        timeLeft = -1;

        base = new Cell[rows][cols];
        actorLocs = new HashMap<>();

        lastMove = null;

        pnlBase = new ComponentPane();
        pnlStage = new ComponentPane();
        getChildren().addAll(pnlBase, pnlStage);
    }

    /**
     * Copies all current state of grid into a new grid.
     *
     * @return a new Grid copy.
     * @throws IOException if something cannot be copied via serialization
     */
    public Grid deepCopy() throws IOException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            ObjectInputStream ois = new ObjectInputStream(new
                    ByteArrayInputStream(baos.toByteArray()));
            return (Grid) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

    /**
     * Gets cell at location.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     * @return the cell at the row/col position.
     */
    public Cell getCellAt(int row, int col) {
        return base[row][col];
    }

    /**
     * Gets cell at location.
     *
     * @param loc the location specifying row, column of cell
     * @return the cell at the row/col position.
     */
    public Cell getCellAt(Location loc) {
        return base[loc.getRow()][loc.getColumn()];
    }

    /**
     * @return the number of columns
     */
    public int getColumns() {
        return cols;
    }

    /**
     * Gets the player object of this level.
     *
     * @return the player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the recording associated with this grid object.
     */
    public Recording getRecording() {
        return recording;
    }

    /**
     * Gets a list of all residents within a particular location
     *
     * @param visitor actor to get relative dominance levels to
     * @param row     row of location
     * @param col     column of location
     * @return a sorted list (by reverse DominanceLevel).
     */
    public List<Actor> getResidents(Actor visitor, int row, int col) {
        Location search = new Location(row, col);
        List<Actor> residents = new ArrayList<>();
        for (Node n : pnlStage.getChildren()) {
            if (n instanceof Actor) {
                Actor a = (Actor) n;
                if (a.getHeadLocation().equals(search)) {
                    residents.add(a);
                }
            }
        }

        residents.sort((a1, a2) -> a2.dominanceLevelFor(visitor).compareTo(
                a1.dominanceLevelFor(visitor)));
        return residents;
    }

    /**
     * @return the amount of food requirements left.
     */
    public int getFoodRequirement() {
        return foodLeft;
    }

    /**
     * @param foodLeft new amount of food to collect
     */
    public void setFoodRequirement(int foodLeft) {
        this.foodLeft = foodLeft;
    }

    /**
     * Decrements time left by one second. This will only do it if the
     * current value is positive.
     */
    public void decrementTime() {
        if (timeLeft > 0) {
            timeLeft--;
        }
    }

    /**
     * Getter method for remaining time.
     *
     * @return the current amount of seconds left on clock.
     */
    public int getTimeLeft() {
        return timeLeft;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    /**
     * Setter method for remaining time
     *
     * @param timeLeft the new amount of seconds left.
     */
    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    /**
     * Determines whether if a row/column location is a valid location, i.e.
     * is a location within this grid's bounds.
     *
     * @param loc the location object representing row/column
     * @return true if valid, false if invalid.
     */
    public boolean isValidLocation(Location loc) {
        return isValidLocation(loc.getRow(), loc.getColumn());
    }

    /**
     * Determines whether if a row/column location is a valid location, i.e.
     * is a location within this grid's bounds.
     *
     * @param row the row of the location.
     * @param col the column of the location.
     * @return true if valid, false if invalid.
     */
    public boolean isValidLocation(int row, int col) {
        return row >= 0 && col >= 0 && row < rows && col < cols;
    }

    /**
     * Increment the amount of food by one unit, and thus the amount of
     * food requirement is decremented.
     */
    public void incrementFood() {
        if (foodLeft > 0) {
            foodLeft--;
        }
    }

    /**
     * Obtains the move that the player has made within the current frame.
     *
     * @return a directional movement, or null if none has been made.
     */
    public Direction getLastMove() {
        return lastMove;
    }

    /**
     * This obtains the rng seed used to make this game have randomly
     * generated elements unique at each game, yet replayable at a later time.
     *
     * @return the current seed for this grid's random number generator.
     * @see StatefulRandom#getSeed()
     */
    public long getRNGSeed() {
        return rng.getSeed();
    }

    /**
     * Sets the grid's random number generator seed to a specified value.
     * This is often done when the program is replaying a particular game.
     *
     * @param seed the new seed to set to
     * @see StatefulRandom#setSeed(long)
     */
    public void setRNGSeed(long seed) {
        rng.setSeed(seed);
    }

    /**
     * Obtains the grid's random number generator so that the 'random' movements
     * of certain components can be replayable by setting random seed.
     *
     * @return grid's random number generator
     */
    public StatefulRandom getRNG() {
        return rng;
    }

    /**
     * @return the number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Moves the actor to a new location.
     *
     * @param comp actor to move
     * @param row  the new row to move to.
     * @param col  the new column to move to.
     * @return true if and only if the actor moved
     */
    public boolean moveActor(Actor comp, int row, int col) {
        return checkVisit(comp, row, col, true);
    }

    /**
     * Moves the player in the specified direction.
     *
     * @param moveDir the direction to move in.
     */
    public void movePlayer(Direction moveDir) {
        Player p = getPlayer();
        if (p == null || p.isMoving()) {
            return;
        }

        lastMove = moveDir;
        p.setHeading(moveDir);
        p.traverseDirection(moveDir);
    }

    /**
     * Checks whether if actor can move to new location, but
     * doesn't actually move it there.
     *
     * @param comp actor to move
     * @param row  the new row to move to.
     * @param col  the new column to move to.
     * @return true if and only if actor can move
     */
    public boolean checkMove(Actor comp, int row, int col) {
        return checkVisit(comp, row, col, false);
    }

    /**
     * Places an actor into the grid.
     *
     * @param comp the component to place into grid.
     * @return true if placed, false if rejected.
     */
    public boolean placeActor(Actor comp) {
        if (comp.getParentGrid() != null) {
            return false;
        }
        if (actorLocs.containsKey(comp)) {
            return false;
        }

        Location loc = comp.getHeadLocation();
        boolean success = checkVisit(comp, loc.getRow(), loc.getColumn(), true);
        if (success) {
            if (comp instanceof Player) {
                player = (Player) comp;
            }

            comp.setParentGrid(this);
            comp.getTrailingLocation().setLocation(loc);
            comp.setTranslateX(loc.getColumn() * CELL_SIZE);
            comp.setTranslateY(loc.getRow() * CELL_SIZE);

            List<Node> children = pnlStage.getChildren();
            DominanceLevel test = comp.dominanceLevelFor(null);

            int insertInd;
            for (insertInd = 0; insertInd < children.size(); insertInd++) {
                if (children.get(insertInd) instanceof Actor) {
                    Actor child = (Actor) children.get(insertInd);
                    if (child.dominanceLevelFor(null).compareTo(test) < 0) {
                        break;
                    }
                }
            }
            children.add(insertInd, comp);
            actorLocs.put(comp, loc);
        }
        return success;
    }

    /**
     * Places a new cell in the cell's specified location.
     *
     * @param comp new cell to put.
     * @return true if it is placed, false otherwise.
     */
    public boolean placeCell(Cell comp) {
        if (comp.getParentGrid() != null) {
            return false;
        }
        if (pnlBase.getChildren().contains(comp)) {
            return false;
        }

        Location loc = comp.getHeadLocation();
        if (base[loc.getRow()][loc.getColumn()] != null) {
            return false;
        }

        comp.setParentGrid(this);
        comp.getTrailingLocation().setLocation(loc);
        comp.setTranslateX(loc.getColumn() * CELL_SIZE);
        comp.setTranslateY(loc.getRow() * CELL_SIZE);

        base[loc.getRow()][loc.getColumn()] = comp;
        pnlBase.getChildren().add(comp);
        return true;
    }

    /**
     * Removes an actor from the grid.
     *
     * @param comp the actor to remove.
     * @return true if and only if this call resulted in a change of the grid.
     */
    public boolean removeActor(Actor comp) {
        if (actorLocs.containsKey(comp)) {
            if (comp == player) {
                player = null;
            }

            comp.setParentGrid(null);
            pnlStage.getChildren().remove(comp);
            actorLocs.remove(comp);
            return true;
        }
        return false;
    }

    /**
     * Removes an cell from the grid.
     *
     * @param comp the cell to remove.
     * @return true if and only if this call resulted in a change of the grid.
     */
    public boolean removeCell(Cell comp) {
        Location loc = comp.getHeadLocation();
        if (!loc.isValidLocation() || loc.getRow() >= rows ||
                loc.getColumn() >= cols) {
            return false;
        }

        if (getCellAt(loc) == comp) {
            comp.setParentGrid(null);
            pnlBase.getChildren().remove(comp);
            base[loc.getRow()][loc.getColumn()] = null;
            return true;
        }
        return false;
    }

    /**
     * Updates frame of all the grid components.
     *
     * @param frame the current frame index.
     */
    public void updateFrame(long frame) {
        if (!recording.isStarted()) {
            recording.startRecording(this);
        }
        recording.updateFrame(frame);
        lastMove = null;

        //Avoid concurrency issues.
        List<Node> base = new ArrayList<>(pnlBase.getChildren());
        List<Node> stage = new ArrayList<>(pnlStage.getChildren());

        for (Node n : base) {
            if (n instanceof Cell) {
                ((Cell) n).updateFrame(frame);
            }
        }

        for (Node n : stage) {
            if (n instanceof Actor) {
                Actor a = (Actor) n;
                a.updateFrame(frame);
                if (a.isDead()) {
                    removeActor(a);
                }
            }
        }
    }

    /**
     * Layouts all the children of this Grid.
     */
    @Override
    protected void layoutChildren() {
        double width = CELL_SIZE * cols;
        double height = CELL_SIZE * rows;

        layoutInArea(pnlBase, 0, 0, width, height, 0, HPos.CENTER,
                VPos.CENTER);
        layoutInArea(pnlStage, 0, 0, width, height, 0, HPos.CENTER,
                VPos.CENTER);
    }

    /**
     * Checks whether if the actor "visitor" can visit this
     * location. It first checks for bounds issues. Then
     * it makes a preliminary check, then executes (if specified)
     * the visiting action. It always starts with the cell,
     * then moves up in actor dominance from highest to lowest
     *
     * @param visitor the actor visitor that will move.
     * @param row     row of the new location.
     * @param col     column of the new location.
     * @param execute true if to execute move, false
     * @return true if the visit is permitted, false otherwise.
     */
    private boolean checkVisit(Actor visitor, int row, int col, boolean execute) {
        if (visitor.isMoving()) {
            return false;
        }

        if (!isValidLocation(row, col)) {
            return false;
        }

        if (base[row][col] != null && !base[row][col].checkPass(visitor)) {
            return false;
        }

        List<Actor> residents = getResidents(visitor, row, col);
        residents.remove(visitor);

        Actor[] master = new Actor[residents.size()];
        Actor[] slave = new Actor[residents.size()];
        for (int i = 0; i < residents.size(); i++) {
            Actor res = residents.get(i);
            if (visitor.dominanceLevelFor(res).compareTo(
                    res.dominanceLevelFor(visitor)) >= 0) {
                master[i] = visitor;
                slave[i] = res;
            } else {
                master[i] = res;
                slave[i] = visitor;
            }
            if (!master[i].checkInteract(slave[i])) {
                return false;
            }
        }

        if (!execute) {
            return true;
        }

        if (base[row][col] != null && !base[row][col].pass(visitor)) {
            return false;
        }

        for (int i = 0; i < residents.size(); i++)
            if (!master[i].interact(slave[i])) {
                return false;
            }

        visitor.getHeadLocation().setLocation(row, col);

        return true;
    }

    /**
     * Reads this object from the provided input stream.
     *
     * @param in the input stream to read from
     * @throws IOException            if an I/O error occurs
     * @throws ClassNotFoundException if a class cannot be found.
     */
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        pnlBase = new ComponentPane();
        pnlStage = new ComponentPane();
        getChildren().addAll(pnlBase, pnlStage);

        for (Cell[] row : base)
            for (Cell cell : row)
                if (cell != null) {
                    pnlBase.getChildren().add(cell);
                }

        PriorityQueue<Actor> stage = new PriorityQueue<>(Comparator.comparing
                (actor -> actor.dominanceLevelFor(null), Comparator
                        .reverseOrder()));
        stage.addAll(actorLocs.keySet());
        while (!stage.isEmpty())
            pnlStage.getChildren().add(stage.poll());
        pnlStage.getChildren().addAll(stage);
    }

    /**
     * Manages a list of maze components and lays them out with appropriate
     * sizes and locations.
     *
     * @author Henry
     */
    private class ComponentPane extends Pane {
        /**
         * Lays all the children components of this layer.
         */
        @Override
        protected void layoutChildren() {
            for (Node child : getManagedChildren()) {
                layoutInArea(child, 0, 0, CELL_SIZE, CELL_SIZE, 0,
                        HPos.CENTER, VPos.CENTER);
            }
        }
    }
}
