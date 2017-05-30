package turtle.core;

import java.io.*;
import java.util.*;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Recording.java
 * 
 * @author Henry Wang
 *
 */
public class Recording implements Serializable
{
    private static final int BUFFER_SIZE = 1024;
    private static final long serialVersionUID = 256701970859720664L;
    private static final Direction[] DIRECTIONS = Direction.values();
    /**
     * CompactMove.java
     *
     * This represents an element move(s) of a recording compacted for
     * repetition.
     * @author Henry Wang
     */
    private static class CompactMove
    {
        private static final byte SIZE_BYTE = 0;
        private static final byte SIZE_SHORT = 1;
        private static final byte SIZE_INT = 2;
        private static final byte SIZE_LONG = 3;
        private static final String[] DIRECTIONS = {"WAIT", "NORTH", "EAST",
                "SOUTH", "WEST"};

        private long repetition;
        private byte direction;

        /**
         * Creates a default new compact-move element
         */
        public CompactMove()
        {
            direction = -1;
            repetition = 0;
        }

        /**
         * Creates a new compact-move element
         * @param repetition the number of repetitions of this move
         * @param direction the direction to move in, or -1 for no move
         */
        public CompactMove(byte direction, long repetition)
        {
            this.repetition = repetition;
            this.direction = direction;
        }

        /**
         * Parses from a data input the compact move.
         * @param in the input to read from.
         * @throws IOException if data is corrupted or has illegal format.
         */
        private void read(DataInput in) throws IOException
        {
            int mode = in.readByte();
            switch (mode)
            {
                case SIZE_BYTE: repetition = in.readByte(); break;
                case SIZE_SHORT: repetition = in.readShort(); break;
                case SIZE_INT: repetition = in.readInt(); break;
                case SIZE_LONG: repetition = in.readLong(); break;
                default: throw new IOException("Illegal data size mode");
            }
            if (repetition < 0)
                throw new IOException("Illegal number of repetitions");

            direction = in.readByte();
            if (direction != -1 && (direction < 0 || direction >= DIRECTIONS
                    .length))
                throw new IOException("Illegal direction");
        }

        /**
         * Writes to a data output the compact move.
         * @param out the output to write to.
         * @throws IOException if an I/O error occurs while writing
         */
        private void write(DataOutput out) throws IOException
        {
            byte mode;
            if (repetition < Byte.MAX_VALUE)
                mode = SIZE_BYTE;
            else if (repetition < Short.MAX_VALUE)
                mode = SIZE_SHORT;
            else if (repetition < Integer.MAX_VALUE)
                mode = SIZE_INT;
            else
                mode = SIZE_LONG;
            out.writeByte(mode);
            switch (mode)
            {
                case SIZE_BYTE: out.writeByte((byte)repetition); break;
                case SIZE_SHORT: out.writeShort((short)repetition); break;
                case SIZE_INT: out.writeInt((int)repetition); break;
                case SIZE_LONG: out.writeLong(repetition); break;
                default: throw new InternalError("Illegal size mode");
            }
            out.writeByte(direction);
        }

        public String toString()
        {
            return DIRECTIONS[direction + 1] + " * " + repetition;
        }
    }

    /**
     * Utility method for compressing data.
     * @param data the data to compress
     * @return a compressed byte array
     */
    private static byte[] compress(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
        deflater.finish();

        byte[] buffer = new byte[BUFFER_SIZE];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            baos.write(buffer, 0, count);
        }
        return baos.toByteArray();
    }

    /**
     * Utility method for decompressing data.
     * @param data the data to compress
     * @return a compressed byte array
     * @throws DataFormatException if the compressed data is not formatted
     * correctly
     */
    private static byte[] decompress(byte[] data) throws DataFormatException
    {
        Inflater inflater = new Inflater();
        inflater.setInput(data);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[BUFFER_SIZE];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            baos.write(buffer, 0, count);
        }
        return baos.toByteArray();
    }

    private HashMap<Long, Integer> moves;
    private long rngSeed;
    private long maxFrame;

    private Grid grid;

    private boolean started;
    private boolean recording;
    
    /**
     * Creates a new blank recording
     */
    public Recording()
    {
        reset();
    }

    /**
     * @return true if this recording has some data loaded, false if this is
     * a blank recording.
     */
    public boolean isLoaded()
    {
        return maxFrame > -1;
    }

    /**
     * @return true if this is recording, false if this is doing a playback
     */
    public boolean isRecording()
    {
        return !recording || !started;
    }

    /**
     * Resets this recording object
     */
    public void reset()
    {
        moves = new HashMap<>();
        started = false;
        maxFrame = -1;
        rngSeed = -1;

        grid = null;
    }

    /**
     * Loads the recording from another recording. This does not start this
     * recording automatically.
     * @param other the other recording to load from.
     * @throws IllegalStateException if other recording is not loaded.
     */
    public void loadRecording(Recording other)
    {
        if (!other.isLoaded())
            throw new IllegalStateException("Other recording not loaded.");
        maxFrame = other.maxFrame;
        moves.clear();
        moves.putAll(other.moves);
        rngSeed = other.rngSeed;
    }

    /**
     * Loads the recording from a series of bytes.
     * @param data a byte representing the recording.
     * @throws IOException if the given data bytes is corrupted.
     */
    public void loadRecording(byte[] data) throws IOException
    {
        try
        {
            ByteArrayInputStream bais = new
                    ByteArrayInputStream(decompress(data));
            DataInputStream dis = new DataInputStream(bais);

            rngSeed = dis.readLong();
            maxFrame = dis.readLong();
            CompactMove[] entries = new CompactMove[dis.readInt()];
            for (int i = 0; i < entries.length; i++)
                (entries[i] = new CompactMove()).read(dis);
            expandMoves(entries);
        }
        catch (DataFormatException e)
        {
            e.printStackTrace();
            throw new IOException("Compression has been corrupted.");
        }
    }

    /**
     * Starts a recording
     * @param grid the grid to record on.
     * @throws IllegalStateException if recording has already started.
     */
    public void startRecording(Grid grid)
    {
        Objects.requireNonNull(grid, "Grid must be non-null.");
        if (started)
            throw new IllegalStateException("This recording has already started.");

        this.grid = grid;
        rngSeed = grid.getRNGSeed();

        recording = true;
        started = true;
        maxFrame = -1;
        moves.clear();
    }

    /**
     * Saves the recording into a series of bytes.
     * @return a byte representing the recording.
     * @throws IOException if an I/O error occurs while saving recording
     */
    public byte[] saveRecording() throws IOException
    {
        if (!isLoaded())
            return new byte[0];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeLong(rngSeed);
        dos.writeLong(maxFrame);
        CompactMove[] entries = compactMoves();
        dos.writeInt(entries.length);
        for (CompactMove move : entries)
            move.write(dos);

        return compress(baos.toByteArray());
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
            throw new IllegalStateException("This recording has already started.");
        if (!isLoaded())
            throw new IllegalStateException("Recording not loaded yet.");

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
        {
            Direction move = grid.getLastMove();
            if (move != null)
                moves.put(frame, move.ordinal());
            maxFrame = frame;
        }
        else if (moves.containsKey(frame))
            grid.movePlayer(DIRECTIONS[moves.get(frame)]);

    }

    /**
     * @return the total number of frames recorded
     */
    public long getRecordingFrames()
    {
        return maxFrame + 1;
    }

    /**
     * Compacts all the player actions/moves.
     * @return a list of compacted moves
     */
    private CompactMove[] compactMoves()
    {
        ArrayList<CompactMove> ret = new ArrayList<>();
        int lastDir = moves.getOrDefault(0L, -1);
        long lastRep = 0;
        long lastFrame = -1;

        TreeMap<Long, Integer> entries = new TreeMap<>(moves);
        for (Map.Entry<Long, Integer> ent : entries.entrySet())
        {
            if (ent.getKey() - 1 < lastFrame)
                throw new AssertionError("Entries going backwards");

            int dir = ent.getValue();
            long dist = ent.getKey() - lastFrame;
            if (dist != 1)
            {
                if (lastDir != -1)
                    ret.add(new CompactMove((byte)lastDir, lastRep));

                lastDir = -1;
                lastRep = dist - 1;
            }

            if (lastDir != dir)
            {
                ret.add(new CompactMove((byte) lastDir, lastRep));
                lastDir = dir;
                lastRep = 0;
            }
            lastRep++;
            lastFrame = ent.getKey();
        }

        if (lastDir != -1)
            ret.add(new CompactMove((byte) lastDir, lastRep));

        long dist = maxFrame - lastFrame;
        if (dist != 0)
            ret.add(new CompactMove((byte) -1, dist));
        return ret.toArray(new CompactMove[0]);
    }

    /**
     * Expands a series of compact-move elements and copies it into our
     * moves list.
     * @param compMoves the list of compact-move elements.
     */
    private void expandMoves(CompactMove[] compMoves)
    {
        moves.clear();

        long frame = 0;
        for (CompactMove move : compMoves)
        {
            for (int i = 0; i < move.repetition; i++)
            {
                if (move.direction != -1)
                    moves.put(frame, (int)move.direction);
                frame++;
            }
        }
    }

}
