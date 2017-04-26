/**
 * Represents the abstract base of all grid components
 * that will be displayed on the Grid.
 * 
 * @author Henry Wang
 * Date: 4/26/17
 * Period: 2
 */
package turtle.comps;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public abstract class Component extends Pane 
{
	public static final TileSet DEFAULT_SET = new TileSet();
	
	private final ImageView img;
	
	/**
	 * Constructs a new component with the image background.
	 */
	protected Component()
	{
		img = new ImageView();
		this.getChildren().add(img);
	}
	
	/**
	 * Layouts the children (i.e. ImageView)
	 * of this Component object
	 */
	@Override
	protected void layoutChildren()
	{
		layoutInArea(img, 0, 0, getWidth(), getHeight(), 0, HPos.CENTER, 
				VPos.CENTER);
	}
	
	/**
	 * Sets the image of this component to the given index.
	 * @param index the index from the TileSet of frames.
	 */
	public void setImageFrame(int index)
	{
		img.setImage(DEFAULT_SET.frameAt(index));
	}
}
