package turtle.editor;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.io.TMXMapReader;
import tiled.util.Base64;
import turtle.comp.ColorType;
import turtle.core.*;
import turtle.file.CompSpec;
import turtle.file.Level;
import turtle.file.LevelPack;

/**
 * TMXToMTP.java
 * 
 * This utility class includes a interactable prompt to help convert TMX files 
 * from "Tiled" into a binary MTP file for the main program to use.
 * 
 * @author Henry
 * Date: 5/6/17
 * Period: 2
 */
@SuppressWarnings("unused")
public class TMXToMTP {
	private static final short IMAGE_TO_SLOT[] = new short[256];
	private static final Pattern LOCATION_FORMAT = Pattern.compile
			("R(\\d+)C(\\d+)");
	private static final TMXMapReader reader = new TMXMapReader();
	
	/**
	 * Enumeration representing the layer type (actor or cell)
	 * to load from.
	 * 
	 * @author Henry
	 * Date: 5/6/17
	 * Period: 2
	 */
	@SuppressWarnings("javadoc")
	public enum LayerType
	{
		ACTOR(Actor.class), CELL(Cell.class);
		
		private Class<? extends Component> clazz;

		LayerType(Class<? extends Component> clazz)
		{
			this.clazz = clazz;
		}

		/**
		 * @return the component type
		 */
		public Class<? extends Component> getCompType()
		{
			return clazz;
		}
	}
	
	/**
	 * Initializes all the image to comp slot index mappings.
	 * @throws Exception if unable to initialize mapping
	 */
	@SuppressWarnings("unchecked")
	public static void initMappings() throws Exception
	{
		Arrays.fill(IMAGE_TO_SLOT, (short)-1);
		Field fld = TileSet.class.getDeclaredField("DEF_COMPS");
		fld.setAccessible(true);

		Class<Component>[] compList = (Class[])fld.get(null);
		for (int i = 0; i < compList.length; i++)
		{
			if (compList[i] != null)
			{
				int defImg = compList[i].getField("DEFAULT_IMAGE").getInt(null);
				IMAGE_TO_SLOT[defImg] = (short)i;
			}
		}
		
	}
	
	@SuppressWarnings("javadoc")
	public static void main(String[] args) throws Exception
	{
		initMappings();
		
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		
		LevelPack pck = new LevelPack("");
		ArrayList<File> selected = new ArrayList<>();
		
		System.out.println("Loading default levels...");
		Pattern def = Pattern.compile("Level\\d{3}.tmx");
		for (File f : new File(".").listFiles())
		{
			if (def.matcher(f.getName()).matches())
			{
				try
				{
					System.out.println("Adding \"" + f.getName() + "\"");
					Level lvl = loadLevel(f);
					pck.addLevel(lvl);
					selected.add(f);
					System.out.println("Level " + lvl.getName() + " added.");
				}
				catch (IOException e)
				{
					e.printStackTrace();
					System.out.println("Unable to load level: " + f);
				}
			}
		}
				
		while (true)
		{
			ArrayList<File> available = new ArrayList<>();
			for (File child : new File(".").listFiles())
			{
				if (child.getName().endsWith(".tmx") && !selected.contains(child))
					available.add(child);
			}
			if (available.isEmpty())
				break;
			
			System.out.println("Available levels to choose from:");
			for (File f : available)
				System.out.println("  -" + f.getName());
			
			System.out.println();
			System.out.print("Type a regex pattern to select levels " + 
					"(or nothing to continue): ");
			
			String pattern = in.nextLine();
			if (pattern == null || pattern.isEmpty())
				break;
			
			Pattern p;
			try
			{
				p = Pattern.compile(pattern);
			}
			catch (PatternSyntaxException e)
			{
				System.out.println("Invalid regex. Try again.\n");
				continue;
			}
			for (File f : available)
			{
				if (p.matcher(f.getName()).matches())
				{
					try
					{
						System.out.println("Adding \"" + f.getName() + "\"");
						Level lvl = loadLevel(f);
						pck.addLevel(lvl);
						selected.add(f);
						System.out.println("Level " + lvl.getName() + " added.");
					}
					catch (IOException e)
					{
						e.printStackTrace();
						System.out.println("Unable to load level: " + f);
					}
					
				}
			}			
		}
		
		System.out.print("Input the level package name: ");
		String name = in.nextLine();
		if (name == null)
			return;
		pck.setName(name);
		
		System.out.print("Input the file to write to: ");
		String file = in.nextLine();
		if (file == null)
			return;
		if (!file.contains("."))
			file += ".mtp";
		pck.savePack(new File(file));
	}

	/**
	 * Loads a TMX level from a file, converting it to a Level object.
	 * 
	 * @param levelFile the TMX level file.
	 * @return a level object describing the file.
	 * @throws Exception if unable to read from file.
	 */
	public static Level loadLevel(File levelFile) throws Exception
	{
		try (FileInputStream fis = new FileInputStream(levelFile))
		{
			Map map = reader.readMap(fis);
			Properties props = map.getProperties();
		
			Level lvl = new Level(props.getProperty("Name", ""), map.getHeight(),
					map.getWidth());
			lvl.setFoodRequirement(parseInt(props.getProperty("FoodReq", "0"), 0));
			lvl.setTimeLimit(parseInt(props.getProperty("TimeLimit", "-1"), -1));
			for (MapLayer lay : map.getLayers())
			{
				if (!(lay instanceof TileLayer))
					continue;
				
				String type = lay.getProperties().getProperty("Type", "");
				if (type.equals("Actor"))
					loadLayer(lvl, (TileLayer)lay, LayerType.ACTOR);
				else if (type.equals("Cell"))
					loadLayer(lvl, (TileLayer)lay, LayerType.CELL);
			}
			return lvl;
		}
	}
	
	/**
	 * Loads a specific tile-layer of this TMX file into the level object
	 * 
	 * @param lvl the level object to load to.
	 * @param lay the layer of the tile to load
	 * @param type the type of the layer to load.
	 */
	public static void loadLayer(Level lvl, TileLayer lay, LayerType type)
	{
		Objects.requireNonNull(lvl);
		Objects.requireNonNull(lay);
		Objects.requireNonNull(type);
		
		List<CompSpec> specs = type == LayerType.ACTOR ? lvl.getActorCompSpecs() :
				lvl.getCellCompSpecs();
		
		for (int r = 0; r < lvl.getRows(); r++)
		{
			for (int c = 0; c < lvl.getCols(); c++)
			{
				Tile tle = lay.getTileAt(c, r);
				if (tle == null)
					continue;
				
				String tleLoc = "tile (Layer " + lay.getName() +  ") @R" + 
						r + "C" + c;
				
				TileSet ts = Component.DEFAULT_SET;
				
				
				HashMap<String, Object> props = new HashMap<>();
				Properties tleProps = new Properties(tle.getProperties());
				Properties instProps = lay.getTileInstancePropertiesAt(c, r);
				if (instProps != null)
					for (Entry<Object, Object> ent : instProps.entrySet())
						tleProps.put(ent.getKey(), ent.getValue());
				
				int id = tle.getId();
				String newId = tleProps.getProperty("LinkToImage");
				if (newId != null)
				{
					tleProps.setProperty("LinkToImage", "");
					id = parseInt(newId, id);
				}
				
				short slot = -1;
				if (id < IMAGE_TO_SLOT.length)
					slot = IMAGE_TO_SLOT[id];
				if (slot == -1)
				{
					System.err.println("WARNING:  " + tleLoc + 
							" has an invalid index " + id);
					continue;
				}
				if (!type.getCompType().isAssignableFrom(ts.componentAt(slot)))
				{
					System.err.println("WARNING: " + tleLoc + " is not a(n) " + 
							type.getCompType().getSimpleName() + " (" + id + ")");
					continue;
				}
				
				for (String prop : tleProps.stringPropertyNames())
				{
					String sval = tleProps.getProperty(prop);
					if (sval.isEmpty())
						continue;
					Object val = parseValue(tleLoc, prop, sval);
					if (val != null)
						props.put(prop, val);
				}
				
				CompSpec spec = new CompSpec(ts, new Location(r, c), slot, 
						props);
				specs.add(spec);
			}
		}
	}
	
	/**
	 * Parses an attribute, from its string version into data.
	 * 
	 * @param loc the location string of this value.
	 * @param prop the property name of this value.
	 * @param val the value itself 
	 * @return an demarshalled object represented by the string
	 */
	private static Object parseValue(String loc, String prop, String val)
	{
		String[] parts = val.split(":", 2);
		if (parts.length != 2)
		{
			System.err.println("WARNING: " + loc + " has an invalid " +
					"property: '" + prop + "' --> '" + val + "'");
			return null;
		}

		switch (parts[0])
		{
			case "color":
				try
				{
					int num = Integer.parseInt(parts[1]);
					ColorType[] values = ColorType.values();
					if (num < 0 || num >= values.length)
					{
						System.err.println("WARNING: " + loc + " has an invalid " +
								"color property: '" + prop + "' --> '" + val + "'");
						return null;
					}
					else
						return values[num];
				}
				catch (NumberFormatException e)
				{
					System.err.println("WARNING: " + loc + " has an invalid " +
							"color property: '" + prop + "' --> '" + val + "'");
					return null;
				}
			case "short":
				try
				{
					return Short.parseShort(parts[1]);
				}
				catch (NumberFormatException e)
				{
					System.err.println("WARNING: " + loc + " has an invalid " +
							"short property: '" + prop + "' --> '" + val + "'");
					return null;
				}
			case "int":
				try
				{
					return Integer.parseInt(parts[1]);
				}
				catch (NumberFormatException e)
				{
					System.err.println("WARNING: " + loc + " has an invalid " +
							"integer property: '" + prop + "' --> '" + val + "'");
					return null;
				}
			case "boolean":
				if ("true".equalsIgnoreCase(parts[1]))
					return true;
				else if ("false".equalsIgnoreCase(parts[1]))
					return false;
				else
				{
					System.out.println("WARNING: " + loc + " has an invalid " +
							"boolean property: '" + prop + "' --> '" + val + "'");
					return null;
				}
			case "string":
				return parts[1];
			case "location":
				Matcher match = LOCATION_FORMAT.matcher(parts[1]);
				if (!match.matches())
				{
					System.err.println("WARNING: " + loc + " has an invalid " +
							"location property: '" + prop + "' --> '" + val + "'");
					return null;
				}
				return new Location(Integer.parseInt(match.group(1)),
						Integer.parseInt(match.group(2)));
			case "class":
				try
				{
					return Class.forName(parts[1]);
				}
				catch (ClassNotFoundException e)
				{
					System.err.println("WARNING: " + loc + " has an invalid " +
							"class property: '" + prop + "' --> '" + val + "'");
					return null;
				}
			case "object":
				try
				{
					byte[] data = Base64.decode(parts[1].toCharArray());
					ObjectInputStream ois = new ObjectInputStream(new
							ByteArrayInputStream(data));
					return ois.readObject();
				}
				catch (Exception e)
				{
					System.err.println("WARNING: " + loc + " has an invalid " +
							"object property: '" + prop + "' --> '" + val + "'");
					return null;
				}
			default:
				System.err.println("WARNING: " + loc + " has an invalid " +
						"property type: '" + prop + "' --> '" + val + "'");
				return null;
		}
			
	}
	
	/**
	 * Parses an integer, catching an exception and printing the error.
	 * @param stringNum the text to parse as a number.
	 * @param def the default value to use if number is not valid.
	 * @return a parsed integer.
	 */
	private static int parseInt(String stringNum, int def)
	{
		try
		{
			return Integer.parseInt(stringNum);
		}
		catch (NumberFormatException e)
		{
			System.err.println("WARNING: '" + def + "' is not a number");
			return def;
		}
	}
}