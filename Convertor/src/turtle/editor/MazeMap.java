package turtle.editor;

import org.mapeditor.core.*;
import org.mapeditor.core.Properties;
import org.mapeditor.util.BasicTileCutter;
import org.mapeditor.util.TileCutter;
import turtle.comp.ColorType;
import turtle.core.Direction;
import turtle.core.Location;
import turtle.file.CompSpec;
import turtle.file.Level;

import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.Map;

import static java.lang.String.format;
import static turtle.core.Component.DEFAULT_SET;
import static turtle.core.Grid.CELL_SIZE;
import static turtle.editor.CompIds.getCompIdsSlot;

/**
 * Extends the TMX map class to specify with our maze format.
 * @author Henry Wang
 */
public class MazeMap extends org.mapeditor.core.Map {
    private static final TileCutter DEF_CUTTER = new BasicTileCutter(CELL_SIZE, CELL_SIZE, 0, 0);

    /**
     * Converts an object value into a string representation.
     * @param val the value
     * @return the string version of this value
     */
    private static String stringify(Object val) {
        if (val instanceof ColorType) {
            return "color:" + ((ColorType)val).ordinal();
        } else if (val instanceof Short) { //TODO: special type for comp id
            return "short:" + val;
        } else if (val instanceof Direction) {
            return "direction:" + ((Direction)val).ordinal();
        } else if (val instanceof Integer) {
            return "int:" + val;
        } else if (val instanceof Boolean) {
            return "boolean:" + val;
        } else if (val instanceof String) {
            return "string:" + val;
        } else if (val instanceof Class) {
            return "class:" + ((Class) val).getName();
        } else if (val instanceof Location) {
            return "location:" + val;
        } else {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(val);
                return "object:" + Base64.getEncoder().encodeToString(baos.toByteArray());
            } catch (Exception e) {
                System.err.println("WARNING: Cannot stringify value: " + val);
                return "";
            }
        }
    }

    private boolean[][] cellUsed;
    private TileLayer cell;
    private ObjectGroup propCell;
    private ObjectGroup actors;

    /**
     * Creates a maze map from a corresponding level layout.
     * @param lvl the level to create from.
     * @throws IOException if an error occurs while reading the tileset.
     */
    public MazeMap(Level lvl) throws IOException {
        super(lvl.getCols(), lvl.getRows());

        //Initialize tile-sets
        //TODO: load other tilesets as well.
        initTileset("tileset.png", DEFAULT_SET);

        //Initialize level properties
        Properties props = getProperties();
        props.setProperty("FoodReq", Integer.toString(lvl.getFoodRequirement()));
        props.setProperty("Name", lvl.getName());
        props.setProperty("TimeLimit", Integer.toString(lvl.getTimeLimit()));
        setProperties(props);

        //Initialize layers
        cellUsed = new boolean[lvl.getCols()][lvl.getRows()];
        cell = initLayer(new TileLayer(lvl.getCols(), lvl.getRows()), "cell");
        addLayer(cell);
        propCell = initLayer(new ObjectGroup(), "cell");
        addLayer(propCell);
        actors = initLayer(new ObjectGroup(), "actor");
        addLayer(actors);

        //Place all the cells
        for (CompSpec spec : lvl.getCellCompSpecs()) {
            addCell(spec);
        }

        //Place all the actors
        for (CompSpec spec : lvl.getActorCompSpecs()) {
            addActor(spec);
        }
    }

    /**
     * Initializes a tileset to this map.
     * @param fileName file name of the tile bitmap
     * @param tsSpecs the tileset specs, which contains the list of components mapped to the tileset
     * @throws IOException if an error occurs while loading tile bitmap
     */
    private void initTileset(String fileName, turtle.core.TileSet tsSpecs) throws
            IOException {
        DEF_CUTTER.reset();
        TileSet ts = new TileSet();
        ts.importTileBitmap(fileName, DEF_CUTTER);

        for (short i = 0; i < tsSpecs.getComponentCount(); i++) {
            CompIds ids = getCompIdsSlot(tsSpecs, i);

            int numVars = ids.getNumVariations();
            for (int j = 0; j < numVars; j++) {
                int tid = ids.getBaseIndex() + j;
                Properties props = deriveProps(new HashMap<>(), ids.getDefaultAttributesFor(tid));
                ts.getTile(tid).setProperties(props);
            }
        }

        addTileset(ts);
    }

    /**
     * Adds an actor represented by this component spec to the map.
     * @param spec the component spec of the actor
     */
    private void addActor(CompSpec spec) {
        Location loc = spec.getLocation();
        int col = loc.getColumn(), row = loc.getRow();

        //TODO: make sure this is a valid actor

        //Find which tile to use
        Map<String, Object> params = spec.getParameters();
        CompIds ids = getCompIdsSlot(DEFAULT_SET, spec.getSlot());
        int tid = ids.findClosestId(params);
        Tile t = getTile(tid);

        //Derive the remaining properties.
        Properties props = deriveProps(ids.getDefaultAttributesFor(tid), params);

        //If props is empty, add it to the tile layer, otherwise add it to object layer
        MapObject obj = new MapObject(col * CELL_SIZE, (row + 1) * CELL_SIZE,
                CELL_SIZE, CELL_SIZE, 0);
        obj.setTile(t);
        obj.setName(format("%s@R%dC%d", ids.getComponentClass().getName(), row, col));
        obj.setProperties(props);
        actors.addObject(obj);
    }

    /**
     * Adds a cell represented by this component spec to the map.
     * @param spec the component spec of the cell.
     */
    private void addCell(CompSpec spec) {
        Location loc = spec.getLocation();
        int col = loc.getColumn(), row = loc.getRow();

        //TODO: make sure this is a valid cell

        //Remove all other cells at this location
        if (cellUsed[col][row]) {
            if (cell.getTileAt(col, row) != null) {
                cell.setTileAt(col, row, null);
            } else {
                Iterator<MapObject> itr = propCell.iterator();
                while (itr.hasNext()) {
                    Rectangle2D bounds = itr.next().getBounds();
                    if ((int) bounds.getX() / CELL_SIZE == col &&
                            (int) bounds.getY() / CELL_SIZE == row) {
                        itr.remove();
                        break;
                    }
                }
            }
        }

        //Mark this location as used.
        cellUsed[col][row] = true;

        //Find which tile to use
        Map<String, Object> params = spec.getParameters();
        CompIds ids = getCompIdsSlot(DEFAULT_SET, spec.getSlot());
        int tid = ids.findClosestId(params);
        Tile t = getTile(tid);

        //Derive the remaining properties.
        Properties props = deriveProps(ids.getDefaultAttributesFor(tid), params);

        //If props is empty, add it to the tile layer, otherwise add it to object layer
        if (props.isEmpty()) {
            cell.setTileAt(col, row, t);
        } else {
            MapObject obj = new MapObject(col * CELL_SIZE, (row + 1) * CELL_SIZE,
                    CELL_SIZE, CELL_SIZE, 0);
            obj.setTile(t);
            obj.setName(format("%s@R%dC%d", ids.getComponentClass().getName(), row, col));
            obj.setProperties(props);
            propCell.addObject(obj);
        }
    }

    /**
     * Derives all the extra properties needed from a set of base and derived attributes. This
     * will also convert all the attributes into the respective string representations.
     *
     * @param base the base (default) attributes
     * @param added the added (default) attributes. An attribute found in base but not in added
     *              will be added by default.
     * @return a set of string property mappings representing the extra properties needed from
     * the base attributes.
     */
    private Properties deriveProps(Map<String, Object> base, Map<String, Object> added) {
        Properties props = new Properties();
        for (Map.Entry<String, Object> entry : added.entrySet()) {
            if (!Objects.equals(base.get(entry.getKey()), entry.getValue())) {
                String val = stringify(entry.getValue());
                if (!val.isEmpty()) {
                    props.setProperty(entry.getKey(), val);
                }
            }
        }
        return props;
    }

    /**
     * Obtains the correct tile from a global id, i.e. from multiple tile-sets.
     * @param gid the global tile ID to get
     * @return a tile at this gid.
     */
    private Tile getTile(int gid) {
        if (gid < 0)
            return null;
        for (TileSet ts : getTileSets()) {
            if (gid > ts.getMaxTileId()) {
                gid -= ts.getMaxTileId() + 1;
            } else {
                return ts.getTile(gid);
            }
        }
        
        return null;
    }

    /**
     * Initializes the tile layer by setting the valid attributes and properties based on the
     * layer type.
     * @param layer the layer object to initialize.
     * @param type the type of the layer, either <tt>ACTOR</tt> or <tt>Cell</tt>.
     * @param <T> a type extending MapLayer
     * @return the initialized layer.
     */
    private <T extends MapLayer> T initLayer(T layer, String type) {
        layer.setName(type);
        layer.getProperties().setProperty("Type", type);
        return layer;
    }
}
