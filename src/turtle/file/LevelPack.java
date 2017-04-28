package turtle.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.UUID;

public class LevelPack
{
	public static final int FIX_HEADER_SIZE = 0x20;
	
	public static final int PACK_FILE_SIG = 0x014D544C; //0x01+'MTL'
	public static final int VERSION_1 = 1;
	
	private final RandomAccessFile raf;
	private final long[] levelOffsets;
	private final UUID levelPackID;
	
	private final ArrayList<Level> levels = new ArrayList<>();
	
	/**
	 * Creates a LevelPack from a file.
	 * @param file the level pack file to read from.
	 * @throws IOException if I/O error occurs or if file is corrupted.
	 */
	public LevelPack(File file) throws IOException
	{
		raf = new RandomAccessFile(file, "r");
		if (PACK_FILE_SIG != raf.readInt())
			throw new IOException("Corrupted file");
		
		if (VERSION_1 != raf.readInt())
			throw new IOException("Unsupported version number");
		
		levelOffsets = new long[raf.readInt()];
		for (int i = 0; i < levelOffsets.length; i++)
		{
			levelOffsets[i] = raf.readLong();
			levels.add(null);
		}
		levelPackID = new UUID(raf.readLong(), raf.readLong());
		
	}
}
