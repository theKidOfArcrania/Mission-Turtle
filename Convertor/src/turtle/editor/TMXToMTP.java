package turtle.editor;

import org.mapeditor.core.*;
import org.mapeditor.io.TMXMapReader;

import turtle.comp.ColorType;
import turtle.core.*;
import turtle.core.TileSet;
import turtle.file.CompSpec;
import turtle.file.Level;
import turtle.file.LevelPack;

import java.io.*;
import java.util.*;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static turtle.core.Component.DEFAULT_SET;
import static turtle.core.Grid.CELL_SIZE;
import static turtle.editor.TMXToMTP.LayerType.getLayer;

/**
 * This utility class includes a interactive prompt to help convert TMX files
 * from "Tiled" into a binary MTP file for the main program to use.
 *
 * @author Henry
 */
@SuppressWarnings("unused")
public class TMXToMTP {
    private static final Pattern LOCATION_FORMAT = Pattern.compile
            ("R(-?\\d+)C(-?\\d+)");
    private static final TMXMapReader reader = new TMXMapReader();
    private static final HashMap<String, LayerType> TYPES = new HashMap<>();

    /**
     * Enumeration representing the layer type (actor or cell)
     * to load from.
     *
     * @author Henry
     */
    public enum LayerType {
        ACTOR(Actor.class), CELL(Cell.class);

        /**
         * Obtains the layer with the specified type name, or <tt>null</tt> if not found. The
         * layer name is case-insensitive.
         * @param type the <tt>String</tt> name of the layer
         * @return the layer type
         */
        public static LayerType getLayer(String type) {
            try {
                return valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        private final Class<? extends Component> clazz;

        /**
         * Constructs a new LayerType
         *
         * @param clazz the class referred to.
         */
        LayerType(Class<? extends Component> clazz) {
            this.clazz = clazz;
        }

        /**
         * @return the component type
         */
        public Class<? extends Component> getCompType() {
            return clazz;
        }
    }

    @SuppressWarnings("javadoc")
    public static void main(String[] args) throws Exception {
        @SuppressWarnings("resource")
        Scanner in = new Scanner(System.in);

        LevelPack pck = new LevelPack("");
        ArrayList<File> selected = new ArrayList<>();

        System.out.println("Loading default levels...");
        Pattern def = Pattern.compile("Level\\d{3}.tmx");
        for (File f : listCurrentDir()) {
            if (def.matcher(f.getName()).matches()) {
                if (addLevel(pck, f))
                    selected.add(f);
            }
        }

        while (true) {
            ArrayList<File> available = new ArrayList<>();
            for (File child : listCurrentDir()) {
                if (child.getName().endsWith(".tmx") && !selected.contains(child)) {
                    available.add(child);
                }
            }
            if (available.isEmpty()) {
                break;
            }

            System.out.println("Available levels to choose from:");
            for (File f : available)
                System.out.println("  -" + f.getName());

            System.out.println();
            System.out.print("Type a regex pattern to select levels " +
                    "(or nothing to continue): ");

            String pattern = in.nextLine();
            if (pattern == null || pattern.isEmpty()) {
                break;
            }

            Pattern p;
            try {
                p = Pattern.compile(pattern);
            } catch (PatternSyntaxException e) {
                System.out.println("Invalid regex. Try again.\n");
                continue;
            }
            for (File f : available) {
                if (p.matcher(f.getName()).matches()) {
                    if (addLevel(pck, f))
                        selected.add(f);
                }
            }
        }

        System.out.print("Input the level package name: ");
        String name = in.nextLine();
        if (name == null) {
            return;
        }
        pck.setName(name);

        System.out.print("Input the file to write to: ");
        String file = in.nextLine();
        if (file == null) {
            return;
        }
        if (!file.contains("")) {
            file += ".mtp";
        }

        try {
            LevelPack prev = new LevelPack(new File(file));
            pck.setLevelPackID(prev.getLevelPackID());
        } catch (Exception e) {
            //Does nothing
        }
        pck.savePack(new File(file));
    }

    /**
     * Converts a {@code org.mapeditor.core.Properties} object into the standard
     * {@code java.util.Properties}.
     * @param props the {@code org.mapeditor.core.Properties} version
     * @return the converted Properties object.
     */
    private static java.util.Properties props(org.mapeditor.core.Properties props) {
        Properties copy = new Properties();
        for (Property prop : props.getProperties()) {
            copy.setProperty(prop.getName(), prop.getValue());
        }
        return copy;
    }

    /**
     * List all files in the current directory, sorted by file name.
     * @return the array of files
     */
    private static File[] listCurrentDir() {
        File[] list = new File(".").listFiles();
        if (list == null)
            list = new File[0];
        Arrays.sort(list, comparing(File::getName));
        return list;
    }

    /**
     * Adds a level to the levelpack from the file
     * @param pack the levelpack to add to.
     * @param lvlFile the file to load
     * @return true if successfully loaded, false if failed.
     */
    private static boolean addLevel(LevelPack pack, File lvlFile) {
        try {
            System.out.println("Adding \"" + lvlFile.getName() + "\"");
            Level lvl = loadLevel(lvlFile);
            pack.addLevel(lvl);
            System.out.println("Level " + lvl.getName() + " added.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unable to load level: " + lvlFile);
            return false;
        }
    }

    /**
     * Loads a TMX level from a file, converting it to a Level object.
     *
     * @param levelFile the TMX level file.
     * @return a level object describing the file.
     * @throws Exception if unable to read from file.
     */
    public static Level loadLevel(File levelFile) throws Exception {
        try (FileInputStream fis = new FileInputStream(levelFile)) {
            org.mapeditor.core.Map map = reader.readMap(fis);
            Properties props = props(map.getProperties());

            Level lvl = new Level(props.getProperty("Name", ""), map.getHeight(),
                    map.getWidth());
            lvl.setFoodRequirement(parseInt(props.getProperty("FoodReq", "0"), 0));
            lvl.setTimeLimit(parseInt(props.getProperty("TimeLimit", "-1"), -1));
            for (MapLayer lay : map.getLayers()) {
                String typeName = lay.getProperties().getProperty("Type", "");
                if (typeName.isEmpty()) {
                    System.out.printf("WARNING: layer '%s' does not have a type.\n", lay.getName());
                    continue;
                }

                LayerType type = getLayer(typeName);
                if (type == null) {
                    System.out.printf("WARNING: layer '%s' does not have a valid type '%s'.\n",
                            lay.getName(), typeName);
                    continue;
                }

                if (lay instanceof TileLayer) {
                    loadTileLayer(lvl, (TileLayer) lay, type);
                } else if (lay instanceof ObjectGroup) {
                    loadObjectLayer(lvl, (ObjectGroup) lay, type);
                } else {
                    System.out.println("WARNING: Invalid layer type.");
                }
            }
            return lvl;
        }
    }


    /**
     * Loads an object-layer of this TMX file into the level object
     *
     * @param lvl  the level object to load to.
     * @param lay  the object-layer to load
     * @param type the type of the layer to load.
     */
    public static void loadObjectLayer(Level lvl, ObjectGroup lay, LayerType type) {
        Objects.requireNonNull(lvl);
        Objects.requireNonNull(lay);
        Objects.requireNonNull(type);

        List<CompSpec> specs = (type == LayerType.ACTOR ? lvl.getActorCompSpecs() :
                lvl.getCellCompSpecs());
        for (MapObject obj : lay.getObjects()) {
            Tile tle = obj.getTile();
            if (tle == null) {
                continue;
            }

            //Make sure that the object snaps into one of the grid cells.
            if (obj.getX() % CELL_SIZE != 0 || obj.getY() % CELL_SIZE != 0 || obj.getX() < 0 ||
                    obj.getY() < CELL_SIZE || obj.getX() > (lvl.getCols() - 1) * CELL_SIZE ||
                    obj.getY() > lvl.getRows() * CELL_SIZE) {
                System.out.printf("WARNING: Invalid object \"%s\" position: (%.1f, %.1f)",
                        obj.getName(), obj.getX(), obj.getY());
                continue;
            }
            if (obj.getWidth() != CELL_SIZE || obj.getHeight() != CELL_SIZE) {
                System.out.printf("WARNING: Invalid object \"%s\" size: %.1fx%.1f",
                        obj.getName(), obj.getWidth(), obj.getHeight());
                continue;
            }

            //Compute location of object
            int r = (int)obj.getY() / CELL_SIZE - 1, c = (int)obj.getX() / CELL_SIZE;
            String objLoc = format("object \"%s\" (layer %s) @R%dC%d", obj.getName(),
                    lay.getName(), r, c);

            //Find component ID
            int id = tle.getId();
            TileSet ts = DEFAULT_SET;
            CompIds ids = CompIds.getCompIdsTile(ts, id);
            if (ids == null) {
                System.err.printf("WARNING: %s has an invalid index %d.\n", objLoc, id);
                continue;
            }

            //Make sure that this is a valid component type.
            if (!type.getCompType().isAssignableFrom(ids.getComponentClass())) {
                System.err.printf("WARNING: %s is not a(n) %s (%d).\n",
                        objLoc, type.getCompType().getSimpleName(), id);
                continue;
            }

            //Add spec
            Properties objProps = props(tle.getProperties());
            objProps.putAll(props(obj.getProperties()));
            specs.add(new CompSpec(ts, new Location(r, c), ids.getSlotID(),
                    extractAttributes(objLoc, objProps)));
        }
    }

    /**
     * Loads a tile-layer of this TMX file into the level object
     *
     * @param lvl  the level object to load to.
     * @param lay  the tile-layer to load
     * @param type the type of the layer to load.
     */
    public static void loadTileLayer(Level lvl, TileLayer lay, LayerType type) {
        Objects.requireNonNull(lvl);
        Objects.requireNonNull(lay);
        Objects.requireNonNull(type);

        List<CompSpec> specs = (type == LayerType.ACTOR ? lvl.getActorCompSpecs() :
                lvl.getCellCompSpecs());
        for (int r = 0; r < lvl.getRows(); r++) {
            for (int c = 0; c < lvl.getCols(); c++) {
                Tile tle = lay.getTileAt(c, r);
                if (tle == null) {
                    continue;
                }

                String tleLoc = format("tile (Layer %s) @R%dC%d", lay.getName(), r, c);

                //Find component ID
                int id = tle.getId();
                TileSet ts = DEFAULT_SET;
                CompIds ids = CompIds.getCompIdsTile(ts, id);
                if (ids == null) {
                    System.err.printf("WARNING: %s has an invalid index %d.\n", tleLoc, id);
                    continue;
                }

                //Make sure that this is a valid component type.
                if (!type.getCompType().isAssignableFrom(ids.getComponentClass())) {
                    System.err.printf("WARNING: %s is not a(n) %s (%d).\n",
                            tleLoc, type.getCompType().getSimpleName(), id);
                    continue;
                }

                //Add spec
                specs.add(new CompSpec(ts, new Location(r, c), ids.getSlotID(),
                        extractAttributes(tleLoc, props(tle.getProperties()))));
            }
        }
    }

    /**
     * Converts all the string attributes into its corresponding object attributes.
     * @param loc a string identifying the location of this cell/actor (for logging errors)
     * @param propAttrs the properties to extract
     * @return a corresponding attribute hashmap of object attributes.
     */
    private static HashMap<String, Object> extractAttributes(String loc, Properties propAttrs) {
        HashMap<String, Object> attrs = new HashMap<>();
        for (String prop : propAttrs.stringPropertyNames()) {
            String sVal = propAttrs.getProperty(prop);
            if (sVal.isEmpty()) {
                continue;
            }
            Object val = parseValue(loc, prop, sVal);
            if (val != null) {
                attrs.put(prop, val);
            }
        }
        return attrs;
    }

    /**
     * Parses an attribute, from its string version into data.
     *
     * @param loc  the location string of this value.
     * @param prop the property name of this value.
     * @param val  the value itself
     * @return an unmarshalled object represented by the string
     */
    private static Object parseValue(String loc, String prop, String val) {
        String[] parts = val.split(":", 2);
        if (parts.length != 2) {
            System.err.println("WARNING: " + loc + " has an invalid " +
                    "property: '" + prop + "' --> '" + val + "'");
            return null;
        }

        switch (parts[0]) {
            case "color":
                try {
                    int num = Integer.parseInt(parts[1]);
                    ColorType[] values = ColorType.values();
                    if (num < 0 || num >= values.length) {
                        System.err.println("WARNING: " + loc + " has an invalid " +
                                "color property: '" + prop + "' --> '" + val + "'");
                        return null;
                    } else {
                        return values[num];
                    }
                } catch (NumberFormatException e) {
                    System.err.println("WARNING: " + loc + " has an invalid " +
                            "color property: '" + prop + "' --> '" + val + "'");
                    return null;
                }
            case "short":
                try {
                    return Short.parseShort(parts[1]);
                } catch (NumberFormatException e) {
                    System.err.println("WARNING: " + loc + " has an invalid " +
                            "short property: '" + prop + "' --> '" + val + "'");
                    return null;
                }
            case "direction":
                try {
                    int num = Integer.parseInt(parts[1]);
                    return Direction.values()[num];
                } catch (Exception e) {
                    System.err.println("WARNING: " + loc + " has an invalid " +
                            "direction property: '" + prop + "' --> '" + val + "'");
                    return null;
                }
            case "int":
                try {
                    return Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    System.err.println("WARNING: " + loc + " has an invalid " +
                            "integer property: '" + prop + "' --> '" + val + "'");
                    return null;
                }
            case "boolean":
                if ("true".equalsIgnoreCase(parts[1])) {
                    return true;
                } else if ("false".equalsIgnoreCase(parts[1])) {
                    return false;
                } else {
                    System.out.println("WARNING: " + loc + " has an invalid " +
                            "boolean property: '" + prop + "' --> '" + val + "'");
                    return null;
                }
            case "string":
                return parts[1];
            case "location":
                Matcher match = LOCATION_FORMAT.matcher(parts[1]);
                if (!match.matches()) {
                    System.err.println("WARNING: " + loc + " has an invalid " +
                            "location property: '" + prop + "' --> '" + val + "'");
                    return null;
                }
                return new Location(Integer.parseInt(match.group(1)),
                        Integer.parseInt(match.group(2)));
            case "class":
                try {
                    return Class.forName(parts[1]);
                } catch (ClassNotFoundException e) {
                    System.err.println("WARNING: " + loc + " has an invalid " +
                            "class property: '" + prop + "' --> '" + val + "'");
                    return null;
                }
            case "object":
                try {
                    byte[] data = Base64.getDecoder().decode(parts[1].getBytes());
                    ObjectInputStream ois = new ObjectInputStream(new
                            ByteArrayInputStream(data));
                    return ois.readObject();
                } catch (Exception e) {
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
     *
     * @param stringNum the text to parse as a number.
     * @param def       the default value to use if number is not valid.
     * @return a parsed integer.
     */
    private static int parseInt(String stringNum, int def) {
        try {
            return Integer.parseInt(stringNum);
        } catch (NumberFormatException e) {
            System.err.println("WARNING: '" + def + "' is not a number");
            return def;
        }
    }
}