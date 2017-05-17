package turtle.recording;

import java.util.HashMap;

import turtle.core.Grid;

/**
 * Recording.java
 * 
 * @author Henry Wang
 *          
 *
 */
public class Recording
{
    private HashMap<Long, Integer> moves;
    
    private Grid g;
    private boolean started;
    private boolean recording;
    private long maxFrame;
    
    /**
     * Creates a new blank recording on a particular map
     * @param g the grid to record/play on.
     */
    public Recording(Grid g)
    {
        moves = new HashMap<>();
        this.g = g;
        started = true;
    }
    
    /**
     * Starts a recording
     * @throws IllegalStateException if recording has already started.
     */
    public void startRecording()
    {
        if (started)
            throw new IllegalStateException("This recording has already started");
        recording = true;
        started = true;
        maxFrame = -1;
        moves.clear();
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
            
        maxFrame = frame;
        
    }
    
    public long getRecordingFrames()
    {
        return maxFrame + 1;
    }
}
