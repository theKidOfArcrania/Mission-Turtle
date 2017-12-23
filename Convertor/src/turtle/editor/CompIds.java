package turtle.editor;

import turtle.attributes.AttributeSet;
import turtle.core.Actor;
import turtle.core.Component;
import turtle.core.DominanceLevel;
import turtle.core.TileSet;

import java.lang.reflect.Method;
import java.util.*;

import static java.util.Objects.requireNonNull;
import static turtle.core.Component.getDefaultImage;

/**
 * @author Henry Wang
 */
public class CompIds {

    private static final HashMap<TileSet, short[]> imageToSlot = new HashMap<>();
    private static final HashMap<Class<? extends Component>, CompIds> compIDs = new HashMap<>();

    /**
     * Fetches component IDs with the specified slot index
     * @param set the tileset
     * @param slot the slot ID
     * @return the comp IDs for it
     */
    public static CompIds getCompIdsSlot(TileSet set, short slot) {
        initMappings(set);
        return compIDs.get(set.componentAt(slot));
    }

    /**
     * Fetches the component IDs with the specified tile ID
     * @param set the tileset
     * @param tid the tile ID
     * @return the component IDs object or null if one does not exist.
     */
    public static CompIds getCompIdsTile(TileSet set, int tid) {
        initMappings(set);

        short[] slots = imageToSlot.get(set);
        if (tid < 0 || tid >= slots.length || slots[tid] == -1) {
            return null;
        }

        return compIDs.get(set.componentAt(slots[tid]));
    }


    /**
     * Initializes all the mappings for a particular tileset.
     * @param set the tileset
     */
    static void initMappings(TileSet set) {
        if (imageToSlot.containsKey(set)) {
            return;
        }

        short[] slots = new short[256];
        Arrays.fill(slots, (short) -1);
        imageToSlot.put(set, slots);
        for (short i = 0; i < set.getComponentCount(); i++) {
            Class<? extends Component> comp = set.componentAt(i);
            compIDs.put(comp, new CompIds(comp, slots, i));
        }
    }

    private final Class<? extends Component> compCls;
    private final short slotID;
    private final int baseInd;
    private final Map<String, Object> defAttrs;
    private final Map<String, Object>[] attrs;
    private final DominanceLevel domLevel;

    /**
     * Constructs the valid comp id's and their corresponding attributes. This will use a bit
     * of reflection to extract the necessary metadata.
     *
     * @param compCls the component class to create
     * @param slots array of slots to fill.
     * @param slotID the component slot number
     */
    @SuppressWarnings("unchecked")
    private CompIds(Class<? extends Component> compCls, short[] slots, short slotID) {
        requireNonNull(compCls);

        this.baseInd = getDefaultImage(compCls);
        this.slotID = slotID;
        this.compCls = compCls;

        Component proto;
        try {
            proto = compCls.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Component class does not have default constructor");
        }

        //Obtain default attributes
        AttributeSet<Component> attrSet = proto.getAttributeSet();
        defAttrs = new HashMap<>();
        for (String attr : attrSet.attributes()) {
            if (!attrSet.isReadOnlyAttribute(attr)) {
                defAttrs.put(attr, attrSet.getAttribute(attr));
            }
        }

        //Get default dominance level
        domLevel = (proto instanceof Actor ? ((Actor) proto).dominanceLevelFor(null) : null);

        //Load attributes.
        ArrayList<HashMap<String, Object>> attrList = new ArrayList<>();
        boolean hasAttributes = false;
        try {
            int ind = baseInd;
            Method mth = compCls.getMethod("attributeOfTile", int.class);
            if (java.util.Map.class.isAssignableFrom(mth.getReturnType())) {
                Object ret;
                while ((ret = mth.invoke(null, ind)) != null) {
                    attrList.add((HashMap<String, Object>) ret);
                    ind++;
                }
                hasAttributes = true;
            }
        } catch (NoSuchMethodException e) {
            hasAttributes = false;
        } catch (Exception e) {
            e.printStackTrace();
            hasAttributes = false;
        }

        if (hasAttributes) {
            attrs = (HashMap<String, Object>[]) (attrList.toArray(new HashMap[0]));
        } else {
            attrs = new HashMap[1];
            attrs[0] = new HashMap<>();
        }

        for (int i = 0; i < attrs.length; i++) {
            //Add to slots
            slots[i + baseInd] = slotID;

            //Normalize attributes
            normalize(attrs[i]);
        }


        for (Map<String, Object> attr : attrs) {
            normalize(attr);
        }
    }
    
    /**
     * Finds the tile ID with the closest matching properties.
     *
     * @param props the properties for this tile
     * @return the closest tile ID.
     */
    public int findClosestId(Map<String, Object> props) {
        props = new HashMap<>(props);
        normalize(props);
        
        int ind = 0;
        int bestMatches = 0;

        for (int i = 0; i < attrs.length; i++) {
            int matches = 0;
            for (Map.Entry<String, Object> prop : attrs[i].entrySet()) {
                if (Objects.equals(prop.getValue(), props.get(prop.getKey()))) {
                    matches++;
                }
            }

            if (matches > bestMatches) {
                bestMatches = matches;
                ind = i;
            }
        }

        return ind + baseInd;
    }

    /**
     * Gets the default properties for a particular component ID.
     * @param id the tile ID
     * @return a map of default props.
     */
    public Map<String, Object> getDefaultAttributesFor(int id) {
        return new HashMap<>(attrs[id - baseInd]);
    }

    public Class<? extends Component> getComponentClass() {
        return compCls;
    }

    public DominanceLevel getDefaultDominanceLevel() {
        return domLevel;
    }
    
    public Map<String, Object> getDefaultAttributes() {
        return new HashMap<>(defAttrs);
    }

    public int getBaseIndex() {
        return baseInd;
    }

    public int getNumVariations() {
        return attrs.length;
    }

    public short getSlotID() {
        return slotID;
    }

    /**
     * Normalizes the set of attributes by filling in the default values
     * @param attrs the attribute map to normalize
     */
    private void normalize(Map<String, Object> attrs) {
        for (Map.Entry<String, Object> prop : defAttrs.entrySet()) {
            if (!attrs.containsKey(prop.getKey())) {
                attrs.put(prop.getKey(), prop.getValue());
            }
        }
    }
}
