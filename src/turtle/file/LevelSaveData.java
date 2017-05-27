package turtle.file;

import turtle.core.Recording;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * LevelSaveData.java
 *
 * This reads/writes the current level status data.
 *
 * @author Henry Wang
 */
public class LevelSaveData
{
    public static final byte MASK_COMPLETE = 1;
    public static final byte MASK_UNLOCKED = 2;

    private byte status;
    private int score;
    private Recording recording;

    private volatile boolean dirty;
    private volatile boolean cleaning;

    /**
     * Constructs a new level save-data
     */
    public LevelSaveData()
    {
        status = 0;
        score = 0;
        recording = new Recording();
    }

    /**
     * Determines whether if level has been completed yet.
     * @return true if completed, false if not completed.
     */
    public boolean isCompleted()
    {
        return (getStatus() & MASK_COMPLETE) > 0;
    }

    /**
     * Determines whether if this level save data is dirty
     * i.e. it requires some asynchronous saving.
     * @return true if dirty, false if clean
     */
    public boolean isDirty()
    {
        return !cleaning && dirty;
    }

    /**
     * Determines whether if level has been unlocked yet.
     * @return true if unlocked, false if not unlocked.
     */
    public boolean isUnlocked()
    {
        return (getStatus() & MASK_UNLOCKED) > 0;
    }

    /**
     * Getter method for status.
     *
     * @return the current value of status.
     */
    public byte getStatus()
    {
        return status;
    }

    /**
     * Sets one or multiple bits of the status based
     *
     * @param mask the bits to set for status.
     * @param set  the new value to set these bits to.
     */
    public void setStatus(int mask, int set)
    {
        int setting = mask & set;
        int other = status & ~mask;
        status = (byte)(setting | other);
        dirty = true;
    }

    /**
     * Getter method for score.
     *
     * @return the current value of score.
     */
    public int getScore()
    {
        return score;
    }

    /**
     * Setter method for score.
     *
     * @param score the value of score to set.
     */
    public void setScore(int score)
    {
        this.score = score;
        dirty = true;
    }

    /**
     * Getter method for recording.
     *
     * @return the current value of recording.
     */
    public Recording getRecording()
    {
        return recording;
    }

    /**
     * Setter method for recording.
     *
     * @param recording the value of recording to set.
     */
    public void setRecording(Recording recording)
    {
        this.recording = recording;
        dirty = true;
    }

    /**
     * Saves a level data to the current position in the data stream
     *
     * @param dos the data stream to write to
     * @throws IOException if an error occurs while saving
     */
    public void saveData(DataOutputStream dos) throws IOException
    {
        try
        {
            cleaning = true;
            dos.writeByte(status);
            dos.writeInt(score);

            byte[] recdata = recording.saveRecording();
            dos.writeInt(recdata.length);
            dos.write(recdata);
            dirty = false;
        }
        finally
        {
            cleaning = false;
        }
    }

    /**
     * Loads a level data from the current position in the data stream
     *
     * @param dis the data stream to read from
     * @throws IOException if the data is corrupted or cannot be fully read.
     */
    public void loadData(DataInputStream dis) throws IOException
    {
        try
        {
            cleaning = true;
            status = dis.readByte();
            score = dis.readInt();

            byte[] recdata = new byte[dis.readInt()];

            recording.reset();
            if (recdata.length != 0)
            {
                dis.readFully(recdata);
                recording.loadRecording(recdata);
            }

            dirty = false;
        }
        finally
        {
            cleaning = false;
        }
    }
}
