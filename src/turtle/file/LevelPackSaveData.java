package turtle.file;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

/**
 * LevelPackSaveData.java
 *
 * This represents all the user status of each level pack, i.e. unlock
 * status, time, and the recording of the user finishing level.
 *
 * @author Henry Wang
 */
public class LevelPackSaveData implements Closeable
{
    private static final int SAVE_FILE_SIG = 0x014D5453; //0x01+'MTS'

    private final AsyncRandomFile arf;
    private final ArrayList<LevelSaveData> levelData;

    private volatile boolean closing;

    /**
     * Creates a new level pack save data and loads in from the file.
     * @param pack the level pack to reference from.
     * @throws IOException if an error occurs while trying to open level pack
     *      save data.
     */
    public LevelPackSaveData(LevelPack pack) throws IOException
    {
        UUID id = pack.getLevelPackID();
        Path dir = Paths.get(System.getProperty("user.home"), ".turtle");
        if (!Files.isDirectory(dir))
        {
            try
            {
                Files.createDirectory(dir);
            }
            catch (IOException e)
            {
                throw new IOException("Unable to create .turtle directory");
            }
        }

        arf = new AsyncRandomFile(dir.resolve(id + ".mts"));

        levelData = new ArrayList<>();
        for (int ind = 0; ind < pack.getLevelCount(); ind++)
            levelData.add(new LevelSaveData());
        loadData();

        Platform.runLater(() -> {
            AnimationTimer timer = new AnimationTimer()
            {
                @Override
                public void handle(long now)
                {
                    if (!arf.isOpen() || closing)
                    {
                        stop();
                        return;
                    }

                    if (checkDirty() && arf.isOpen())
                        updateData();
                }
            };
            timer.start();
        });
    }

    /**
     * Obtains the level save data at the particular index
     *
     * @param level the level index to get
     * @return a level save data.
     */
    public LevelSaveData getLevel(int level)
    {
        return levelData.get(level);
    }

    /**
     * Explicitly updates the save data file. This will automatically be
     * called at every so increments if any of the level packs are dirty.
     */
    public void updateData()
    {
        try
        {
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(arf
                    .obtainOutputStream(0)));
            dos.writeInt(SAVE_FILE_SIG);
            dos.writeInt(levelData.size());

            for (LevelSaveData level : levelData)
                level.saveData(dos);
        }
        catch (IOException e)
        {
            //Shouldn't happen
            throw new Error(e);
        }
    }

    /**
     * Closes the underlying file and any associated locks to it, and saves
     * any remaining dirty data.
     * @throws IOException if an error occurs while closing.
     */
    public void close() throws IOException
    {
        if (closing || !arf.isOpen())
            return;
        closing = true;
        if (checkDirty())
            updateData();
        arf.close();
    }

    /**
     * Forces the file to be closed without saving any current cached data.
     */
    public void forceClose() throws IOException
    {
        if (closing || !arf.isOpen())
            return;
        closing = true;
        arf.close();
    }

    /**
     * Checks if any of the level save-datas are dirty.
     * @return true if dirty, false if not dirty.
     */
    private boolean checkDirty()
    {
        for (LevelSaveData level : levelData)
        {
            if (level.isDirty())
                return true;
        }
        return false;
    }

    /**
     * Loads from the save data file.
     * @throws IOException if an error occurs while trying to open level pack
     *      save data.
     */
    private void loadData() throws IOException
    {
        //This is an empty file.
        if (arf.length() == 0)
            return;

        DataInputStream dis = new DataInputStream(new BufferedInputStream(arf
                .obtainInputStream(0)));

        if (dis.readInt() != SAVE_FILE_SIG)
            throw new IOException("Save data signature is corrupted.");

        int levels = Math.min(dis.readInt(), levelData.size());
        for (int level = 0; level < levels; level++)
            levelData.get(level).loadData(dis);
    }
}
