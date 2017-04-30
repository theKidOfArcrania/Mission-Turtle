/**
 * LevelPack.java
 * 
 * This represents a collection of levels that could be stored to or loaded
 * from a file. 
 * 
 * @author Henry Wang
 * Date: 4/28/17
 * Period: 2
 */
package turtle.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.UUID;

public class LevelPack
{
	public static final int FIXED_HEADER_SIZE = 0x20;
	
	public static final int PACK_FILE_SIG = 0x014D544C; //0x01+'MTL'
	public static final int VERSION_1 = 1;
	
	private final boolean loadedMode;
	
	private final RandomAccessFile raf;
	private final long[] levelOffsets;
	private final UUID levelPackID;
	private String name;
	
	private final ArrayList<Level> levels;
	
	/**
	 * Creates a LevelPack from a file.
	 * @param file the level pack file to read from.
	 * @throws IOException if I/O error occurs or if file is corrupted.
	 */
	public LevelPack(File file) throws IOException
	{
		levels = new ArrayList<>();
		loadedMode = true;
		
		raf = new RandomAccessFile(file, "r");
		if (PACK_FILE_SIG != raf.readInt())
			throw new IOException("Corrupted file");
		
		if (VERSION_1 != raf.readInt())
			throw new IOException("Unsupported version number");
		
		levelOffsets = new long[raf.readInt()];
		for (int i = 0; i < levelOffsets.length; i++)
		{
			levelOffsets[i] = raf.readLong();
			levels.add(new Level(levelOffsets[i]));
		}
		levelPackID = new UUID(raf.readLong(), raf.readLong());
		
		name = raf.readUTF();
	}
	
	/**
	 * Creates a new editable LevelPack, initially with no levels
	 */
	public LevelPack()
	{
		this.levelOffsets = null;
		this.levelPackID = UUID.randomUUID();
		this.levels = new ArrayList<>();
		this.loadedMode = false;
		this.raf = null;
	}
	
	/**
	 * @return name of level pack
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Saves the level pack into a file.
	 * @param file the file to save to.
	 * @throws IllegalStateException if LevelPack is loaded from file or 
	 * 	if not all levels are loaded.
	 */
	public void savePack(File file) throws IOException
	{
		if (loadedMode)
			throw new IllegalStateException("Level pack is in load mode");
		for (Level lvl : levels)
			if (!lvl.isLoaded())
				throw new IllegalStateException("Levels are not all loaded");
		
		RandomAccessFile raf = new RandomAccessFile(file, "w");
		raf.writeInt(PACK_FILE_SIG);
		raf.writeInt(VERSION_1);
		raf.writeInt(levels.size());
		
		long levelOffsets = raf.getFilePointer();
		raf.skipBytes(levels.size() * Long.BYTES);
		raf.writeLong(levelPackID.getMostSignificantBits());
		raf.writeLong(levelPackID.getLeastSignificantBits());
		raf.writeUTF(name);
		
		long[] offsets = new long[levels.size()];
		for (int i = 0; i < levels.size(); i++)
		{
			offsets[i] = raf.getFilePointer();
			levels.get(i).saveLevel(raf);
		}
		
		raf.seek(levelOffsets);
		for (long off : offsets)
			raf.writeLong(off);
	}
	
	/**
	 * @param name new name of level pack to set.
	 * @throws IllegalStateException if this LevelPack loads from a file.
	 */
	public void setName(String name)
	{
		if (loadedMode)
			throw new IllegalStateException("Level pack is read-only in load "
					+ "mode");
		this.name = name;
	}

	/**
	 * Loads a level from file and returns it. 
	 * @param index index of level to load.
	 * @return the loaded level.
	 * @throws IllegalStateException if this LevelPack does not load from a 
	 * 		file.
	 */
	public Level loadLevel(int index) throws IOException
	{
		if (!loadedMode)
			throw new IllegalStateException("No level to load");
		
		Level lvl = levels.get(index);
		if (!lvl.isLoaded())
			lvl.loadLevel(raf);
		return lvl;
	}
	
	
	
	/**
	 * Obtains the level at the index (does not necessarily load it if not 
	 * already loaded).
	 * @param index the index of level to get.
	 * @return the level at index.
	 */
	public Level getLevel(int index)
	{
		return levels.get(index);
	}
	
	/**
	 * Sets a level at the index.
	 * @param index the index of level to set
	 * @param lvl the new level object to set to.
	 * @throws IllegalStateException if this LevelPack loads from a file.
	 * @throws IllegalArgumentException if this level already belongs to 
	 * 		another level-pack.
	 */
	public void setLevel(int index, Level lvl)
	{
		if (loadedMode)
			throw new IllegalStateException("Level pack is read-only in load "
					+ "mode.");
		if (lvl.parent != null)
			throw new IllegalArgumentException("This level already is in " +
					"another level pack.");
		levels.set(index, lvl).parent = null;
		lvl.parent = this;
	}
	
	/**
	 * Adds a new level.
	 * @param lvl the new level object to add.
	 * @throws IllegalStateException if this LevelPack loads from a file.
	 * @throws IllegalArgumentException if this level already belongs to 
	 * 		another level-pack.
	 */
	public void addLevel(Level lvl)
	{
		if (loadedMode)
			throw new IllegalStateException("Level pack is read-only in load "
					+ "mode");
		if (lvl.parent != null)
			throw new IllegalArgumentException("This level already is in " +
					"another level pack.");
		levels.add(lvl);
		lvl.parent = this;
	}
	
	/**
	 * Removes a level at index.
	 * @param ind the index to remove at.
	 * @throws IllegalStateException if this LevelPack loads from a file.
	 */
	public void removeLevel(int ind)
	{
		if (loadedMode)
			throw new IllegalStateException("Level pack is read-only in load "
					+ "mode");
		levels.remove(ind).parent = null;
	}
}
