package turtle.core;

import java.util.HashMap;
import java.util.Objects;

import turtle.core.Grid;
import turtle.file.Level;

/**
 * Recording.java
 * 
 * @author Henry Wang
 *
 */
public class Recording
{
    private HashMap<Long, Integer> moves;
    
    private Grid grid;
    private long rngSeed;

    private boolean started;
    private boolean recording;
    private long maxFrame;
    
    /**
     * Creates a new blank recording
     */
    public Recording()
    {
        moves = new HashMap<>();
        started = false;
        maxFrame = -1;
        rngSeed = -1;
    }
    
    /**
     * Starts a recording
     * @param grid the grid to record on.
     * @throws IllegalStateException if recording has already started.
     */
    public void startRecording(Grid grid)
    {
        Objects.requireNonNull(grid, "Grid must be non-null");
        if (started)
            throw new IllegalStateException("This recording has already started");

        this.grid = grid;
        rngSeed = grid.getRNGSeed();

        recording = true;
        started = true;
        maxFrame = -1;
        moves.clear();
    }

    /**
     * Starts a play-back on a particular grid
     * @param grid the grid to play on.
     * @throws IllegalStateException if recording has already started.
     * @throws IllegalStateException if there is no recording loaded.
     */
    public void startPlayback(Grid grid)
    {
        Objects.requireNonNull(grid, "Grid must be non-null");
        if (started)
            throw new IllegalStateException("This recording has already started");
        if (maxFrame == -1)
            throw new IllegalStateException("No recording has been loaded " +
                    "yet.");

        this.grid = grid;
        grid.setRNGSeed(rngSeed);

        recording = false;
        started = true;
    }

    /**
     * Stops the recording/playback.
     */
    public void stop()
    {
        started = false;
    }
    
    /**
     * @return true if we started a recording/playback, false otherwise.
     */
    public boolean isStarted()
    {
        return started;
    }
    
    /**
     * Updates the current frame, executing the recording or plays back player action.
     * @param frame the current frame count
     * @throws IllegalStateException if recording has not started yet.
     */
    public void updateFrame(long frame)
    {
        if (!started)
            throw new IllegalStateException("This recording has not started yet.");
        if (recording)
            moves.put(frame, grid.getLastMove());
        else if (moves.containsKey(frame))
            grid.movePlayer(moves.get(frame));
        maxFrame = frame;
    }
    
    public long getRecordingFrames()
    {
        return maxFrame + 1;
    }
}
