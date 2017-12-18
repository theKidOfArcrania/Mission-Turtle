package turtle.file;

import turtle.core.Recording;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This reads/writes the current level status data.
 *
 * @author Henry Wang
 */
public class LevelSaveData {
    public static final byte MASK_COMPLETE = 1;
    public static final byte MASK_UNLOCKED = 2;
    public static final byte MASK_SOLUTION_INVALID = 4;

    private byte status;
    private int score;
    private byte[] recordingData;

    private volatile boolean dirty;
    private volatile boolean cleaning;

    /**
     * Constructs a new level save-data
     */
    public LevelSaveData() {
        status = 0;
        score = 0;
        recordingData = new byte[0];
        cleaning = dirty = false;
    }


    /**
     * Determines whether if level has been completed yet.
     *
     * @return true if completed, false if not completed.
     */
    public boolean isCompleted() {
        return (getStatus() & MASK_COMPLETE) > 0;
    }

    /**
     * Determines whether if this level save data is dirty
     * i.e. it requires some asynchronous saving.
     *
     * @return true if dirty, false if clean
     */
    public boolean isDirty() {
        return !cleaning && dirty;
    }

    /**
     * Determines if the solution provided is a valid solution. This does not
     * check for the presence of a solution, but whether if the invalid
     * solution flag bit is set.
     *
     * @return true if invalid, false if valid.
     */
    public boolean isSolutionInvalid() {
        return (getStatus() & MASK_SOLUTION_INVALID) > 0;
    }

    /**
     * Determines whether if level has been unlocked yet.
     *
     * @return true if unlocked, false if not unlocked.
     */
    public boolean isUnlocked() {
        return (getStatus() & MASK_UNLOCKED) > 0;
    }

    /**
     * Getter method for status.
     *
     * @return the current value of status.
     */
    public byte getStatus() {
        return status;
    }

    /**
     * Sets one or multiple bits of the status based
     *
     * @param mask the bits to set for status.
     * @param set  the new value to set these bits to.
     */
    public void setStatus(int mask, int set) {
        int before = status & mask;
        int setting = set & mask;
        int other = status & ~mask;
        status = (byte) (setting | other);
        dirty |= before != setting;
    }

    /**
     * Getter method for score.
     *
     * @return the current value of score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Setter method for score.
     *
     * @param score the value of score to set.
     */
    public void setScore(int score) {
        int before = this.score;
        this.score = score;
        dirty |= before != score;
    }

    /**
     * Obtains a new recordingData loaded from the current data.
     *
     * @return the recorded moves
     * @throws IOException if an error occurs while parsing recordingData.
     */
    public Recording createRecording() throws IOException {
        Recording rec = new Recording();
        if (recordingData.length > 0) {
            rec.loadRecording(recordingData);
        }
        return rec;
    }

    /**
     * Setter method for recordingData.
     *
     * @param recordingData a recording to set to
     */
    public void setRecordingData(Recording recordingData) {
        try {
            this.recordingData = recordingData.saveRecording();
            setStatus(MASK_SOLUTION_INVALID, 0);
            dirty = true;
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    /**
     * Saves a level data to the current position in the data stream
     *
     * @param dos the data stream to write to
     * @throws IOException if an error occurs while saving
     */
    public void saveData(DataOutputStream dos) throws IOException {
        try {
            cleaning = true;
            dos.writeByte(status);
            dos.writeInt(score);

            dos.writeInt(recordingData.length);
            dos.write(recordingData);
            dirty = false;
        } finally {
            cleaning = false;
        }
    }

    /**
     * Loads a level data from the current position in the data stream
     *
     * @param dis the data stream to read from
     * @throws IOException if the data is corrupted or cannot be fully read.
     */
    public void loadData(DataInputStream dis) throws IOException {
        try {
            cleaning = true;
            status = dis.readByte();
            score = dis.readInt();

            recordingData = new byte[dis.readInt()];
            if (recordingData.length != 0) {
                dis.readFully(recordingData);
            }

            dirty = false;
        } finally {
            cleaning = false;
        }
    }
}
