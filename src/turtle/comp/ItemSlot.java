package turtle.comp;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import turtle.core.TileSet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * ItemUI.java
 * <p>
 * This represents a single item slot representing a multiplicity of a type of
 * item. This is used by player to keep track of items.
 *
 * @author Henry Wang
 *         Date: 5/5/17
 *         Period: 2
 */
public class ItemSlot extends Pane implements Serializable
{
    private static final int SHADOW_RADIUS = 10;
    private static final double SHADOW_SPREAD = .8;
    private static final double HIGHLIGHT_SPREAD = .6;
    private static final int HIGHLIGHT_RADIUS = 30;

    private static final int ITEM_SIZE = 30;
    
    private static final long serialVersionUID = 2756007054208351589L;

    private final ArrayList<Item> items;
    private transient ImageView itemRep;
    private transient Label number;

    /**
     * Creates a new ItemUI and initializes UI.
     */
    public ItemSlot()
    {
        items = new ArrayList<>();
        initUI();
    }

    /**
     * Initializes the UI of a item-slot
     */
    private void initUI()
    {
        itemRep = new ImageView();

        itemRep.setFitWidth(ITEM_SIZE);
        itemRep.setFitHeight(ITEM_SIZE);

        number = new Label();
        number.getStyleClass().add("small");

        DropShadow shadow = new DropShadow(SHADOW_RADIUS, Color.BLACK);
        shadow.setSpread(SHADOW_SPREAD);
        number.setEffect(shadow);

        this.getChildren().addAll(itemRep, number);

        DropShadow highlight = new DropShadow(HIGHLIGHT_RADIUS, Color.WHITE);
        highlight.setSpread(HIGHLIGHT_SPREAD);
        this.setEffect(highlight);
    }

    /**
     * Adds a new item to this ui panel. This will only add
     * items of the same type.
     *
     * @param itm the item to add.
     * @return true if item was added, false if not.
     */
    public boolean addItem(Item itm)
    {
        if (items.size() == 0)
        {
            TileSet ts = itm.getTileSet();
            itemRep.setImage(ts.getImageSet());
            itemRep.setViewport(ts.frameAt(itm.getItemImage()));
        } else if (!items.get(0).identical(itm))
            return false;

        items.add(itm);
        number.setText("" + items.size());

        return true;
    }

    /**
     * Removes an item from this item UI.
     *
     * @param itm the item to remove.
     * @return true if an item has been removed.
     */
    public boolean removeItem(Item itm)
    {
        boolean removed = items.remove(itm);
        number.setText("" + items.size());
        return removed;
    }

    /**
     * Determines whether if this item UI slot is empty.
     *
     * @return true if empty, false if filled.
     */
    public boolean isEmpty()
    {
        return items.isEmpty();
    }

    /**
     * Layouts the image view within this component to fit the entire
     * screen and center alignment.
     */
    @Override
    protected void layoutChildren()
    {
        double width = getWidth();
        double height = getHeight();
        double numWidth = number.prefWidth(-1);
        double numHeight = number.prefHeight(-1);

        layoutInArea(itemRep, 0, 0, width, height, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(number, (width + ITEM_SIZE - numWidth) / 2,
                (height + ITEM_SIZE - numHeight) / 2, numWidth, numHeight, 0,
                HPos.CENTER, VPos.CENTER);
    }

    /**
     * Reads this object from the provided input stream.
     *
     * @param in the input stream to read from
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if a class cannot be found.
     */
    private void readObject(ObjectInputStream in)
         throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        initUI();

        if (items.size() > 0)
        {
            Item itm = items.get(0);
            TileSet ts = itm.getTileSet();
            itemRep.setImage(ts.getImageSet());
            itemRep.setViewport(ts.frameAt(itm.getItemImage()));
        }
        number.setText("" + items.size());
    }
}
