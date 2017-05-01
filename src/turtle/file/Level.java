/**
 * Level.java
 * 
 * This represents a level that can be loaded from a file, or edited by the
 * user (not simultaneously, though) that could later be copied into a
 * Grid to be played.
 * 
 * @author Henry Wang
 * Period: 2
 * Date: 4/27/17 
 */

package turtle.file;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import turtle.core.Actor;
import turtle.core.Cell;
import turtle.core.Component;
import turtle.core.Grid;
import turtle.core.Location;
import turtle.core.TileSet;

public class Level
{
	private boolean loaded;
	private long offset;
	
	private String name;
	private int rows;
	private int cols;
	
	private int timeLimit;
	private int foodReq;
	
	private ArrayList<CompSpec> cells = new ArrayList<>();
	private ArrayList<CompSpec> actors = new ArrayList<>();
	
	//Set by LevelPack.
	LevelPack parent; 
	
	/**
	 * Constructs a new level from a file offset pointing to level.
	 * @param offset the file offset that points to level.
	 * @throws IllegalArgumentException if a negative file offset is specified.
	 */
	public Level(long offset) 
	{
		if (offset < 0)
			throw new IllegalArgumentException("Negative file offset");
		this.parent = null;
		this.offset = offset;
		this.rows = -1;
		this.cols = -1;
		this.foodReq = 0;
		this.timeLimit = -1;
	}
	
	/**
	 * Constructs a new blank level, that can be edited and saved.
	 * @param name the name of this level.
	 * @param rows the number of rows.
	 * @param cols the number of columns.
	 */
	public Level(String name, int rows, int cols)
	{
		this.parent = null;
		this.name = name;
		this.rows = rows;
		this.cols = cols;
		this.loaded = true;
		this.offset = -1;
		this.foodReq = 0;
		this.timeLimit = -1;
	}
	
	/**
	 * Creates the grid that is specified by this level data.
	 * @return an interactable live Grid.
	 */
	public Grid createLevel()
	{
		Grid g = new Grid(rows, cols);
		System.out.println("HELLO");
		for (CompSpec spec : cells)
			g.placeCell((Cell)spec.createComponent());
		for (CompSpec spec : actors)
			g.placeActor((Actor)spec.createComponent());
		return g;
	}
	
	/**
	 * Obtains the list of actor component specs (if level is editable,
	 * modifying this list will modify the the level's list).
	 * 
	 * @return a list (or copy, depending on editable state) of the actor specs
	 */
	public List<CompSpec> getActorCompSpecs()
	{
		if (isEditable())
			return actors;
		else
			return new ArrayList<>(actors);
	}
	
	/**
	 * Obtains the list of cell component specs (if level is editable,
	 * modifying this list will modify the the level's list).
	 * 
	 * @return a list (or copy, depending on editable state) of the cell specs
	 */
	public List<CompSpec> getCellCompSpecs()
	{
		if (isEditable())
			return cells;
		else
			return new ArrayList<>(cells);
	}
	
	/**
	 * @return the number of cols
	 */
	public int getCols()
	{
		return cols;
	}
	
	/**
	 * @return the food requirement count
	 */
	public int getFoodRequirement()
	{
		return foodReq;
	}

	/**
	 * @return the parent level pack
	 */
	public LevelPack getPack()
	{
		return parent;
	}
	
	/**
	 * @return the current level name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * @return the number of rows
	 */
	public int getRows()
	{
		return rows;
	}

	/**
	 * @return the time limit
	 */
	public int getTimeLimit()
	{
		return timeLimit;
	}

	/**
	 * Determines whether if this level object is editable or not.
	 * @return true if editable, false if read-only.
	 */
	public boolean isEditable()
	{
		return offset == -1;
	}

	/**
	 * Determines whether if this level object has already been loaded.
	 * @return true if loaded, false if it is not loaded.
	 */
	public boolean isLoaded()
	{
		return loaded;
	}

	/**
	 * Loads a level from the file into memory.
	 * @param raf
	 * @throws IllegalStateException if this is called on a level does not
	 * 		load from file.
	 * @throws IOException if the level file is corrupted.
	 * @throws IllegalStateException if this level does not load from a file.
	 */
	public void loadLevel(RandomAccessFile raf) throws IOException
	{
		if (offset == -1)
			throw new IllegalStateException("Not a level from file.");

		if (loaded)
			return;
		
		raf.seek(offset);
		
		//Read header.
		rows = raf.readInt();
		cols = raf.readInt();
		
		name = raf.readUTF();
		foodReq = raf.readInt();
		timeLimit = raf.readInt();		
		
		//Read comp specs
		cells.clear();
		actors.clear();
		int numCells = raf.readInt();
		int numActors = raf.readInt();
		for (int i = 0; i < numCells; i++)
			cells.add(readCompSpec(raf, Cell.class));
		for (int i = 0; i < numActors; i++)
			actors.add(readCompSpec(raf, Actor.class));
		
		loaded = true;
	}

	/**
	 * Stores the level at the current position in file.
	 * @param raf the file to write to.
	 * @throws IllegalStateException if level is not loaded yet.
	 */
	public void saveLevel(RandomAccessFile raf) throws IOException
	{
		if (!loaded)
			throw new IllegalStateException("Not loaded yet.");
		
		//Write header.
		raf.write(rows);
		raf.write(cols);
		
		raf.writeUTF(name);
		raf.write(foodReq);
		raf.write(timeLimit);
		
		raf.write(cells.size());
		raf.write(actors.size());
		
		//Write comp-specs
		for (CompSpec spec : cells)
			writeCompSpec(raf, spec);
		for (CompSpec spec : actors)
			writeCompSpec(raf, spec);
	}

	/**
	 * @param foodReq the new food requirement count
	 * @throws IllegalStateException if level is not editable
	 * @throws IllegalArgumentException if new food count is negative.
	 */
	public void setFoodReqirement(int foodReq)
	{
		if (!isEditable())
			throw new IllegalStateException("Level is not editable!");
		if (foodReq < 0)
			throw new IllegalArgumentException("Food requirement must be " 
					+ "non-negative");
		this.foodReq = foodReq;
	}

	/**
	 * @param name the new level name
	 * @throws IllegalStateException if level is not editable
	 */
	public void setName(String name)
	{
		if (!isEditable())
			throw new IllegalStateException("Level is not editable!");
		this.name = name;
	}
	
	/**
	 * @param timeLimit the new time limit
	 * @throws IllegalStateException if level is not editable
	 * @throws IllegalArgumentException if time limit is not positive or -1.
	 */
	public void setTimeLimit(int timeLimit)
	{
		if (!isEditable())
			throw new IllegalStateException("Level is not editable!");
		if (timeLimit == 0 || timeLimit < -1)
			throw new IllegalArgumentException("Illegal time value");
		this.timeLimit = timeLimit;
	}
	
	/**
	 * Reads component specifications at the current location
	 * @param raf the file to read from
	 * @param expectedType the expected class type of this component.
	 * @return a parsed component specification
	 * @throws IOException if the component spec data is corrupted.
	 */
	private CompSpec readCompSpec(RandomAccessFile raf, 
			Class<? extends Component> expectedType) throws IOException
	{
		Location loc = new Location(raf.readInt(), raf.readInt());
		short compID = raf.readShort();
		byte[] initData = new byte[raf.read()];
		raf.read(initData);
		
		TileSet ts = Component.DEFAULT_SET;
		if (!expectedType.isAssignableFrom(ts.componentAt(compID)))
			throw new IOException("Unexpected component type");
		return new CompSpec(ts, loc, compID, initData);
	}
	
	/**
	 * Writes a component specification to the current location
	 * @param raf the file to write to
	 * @param component specification to write
	 */
	private void writeCompSpec(RandomAccessFile raf, CompSpec spec) throws 
		IOException
	{
		Location loc = spec.getLocation();
		raf.write(loc.getRow());
		raf.write(loc.getColumn());
		raf.write(spec.getSlot());
		
		byte[] initData = spec.storeParameters();
		raf.write(initData.length);
		raf.write(initData);
	}
}
