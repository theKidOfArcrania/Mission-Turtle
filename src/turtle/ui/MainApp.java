package turtle.ui;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import turtle.file.LevelPack;

public class MainApp
{ 
	private   
	
	public static void main(String[] args)
	{
		
	}
	
	/**
	 * Saves the information that the user has completed a particular level 
	 * from a particular level pack.
	 * 
	 * @param pack the level pack of the level
	 * @param level the level index to save
	 * @param time the time completed in.
	 * @throws IOException if an error occurs while saving level status
	 */
	void completeLevel(LevelPack pack, int level, int time) throws IOException
	{
		File dir = new File(System.getProperty("user.home"), ".turtle");
		dir.mkdir();
		
		try (RandomAccessFile raf = new RandomAccessFile(new File(
				dir, pack.getLevelPackID() + ".sav"), "rw"))
		{
			long offset = Long.BYTES * level;
			if (raf.length() < offset + Long.BYTES)
				raf.setLength(offset + Long.BYTES);
			raf.seek(offset);
			raf.writeInt(1);
			raf.writeInt(time);
		}
	}
	
	boolean 
}
