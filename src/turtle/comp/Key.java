package turtle.comp;

import java.util.Map;

/**
 * Key.java
 * 
 * This item can be collected by the player in order to unlock a door
 * of the same color.
 * 
 * @author Henry Wang
 * Date: 5/3/17
 * Period: 2
 */
public class Key extends Item
{
	public static final int DEFAULT_IMAGE = 45;
	private static final int KEY_OFFSET_IMAGE = 45;
	
	private ColorType color;

	/**
	 * Constructs a new Key by initializing UI. It will
	 * by default initialize to the color RED.
	 */
	public Key()
	{
		setColor(ColorType.RED);
	}
	
	/**
	 * @return the color of this key
	 */
	public ColorType getColor()
	{
		return color;
	}

	/**
	 * @param color the new color to set for this key
	 * @throws NullPointerException if the color supplied is null.
	 */
	public void setColor(ColorType color)
	{
		if (color == null)
			throw new NullPointerException();
		setImageFrame(color.getImageFrame(KEY_OFFSET_IMAGE));
		this.color = color;
	}

	/**
	 * Sets a series of parameters for this key actor. This
	 * class has one parameter attribute that has functionality:
	 * <table>
	 *   <tr>
	 *     <th>Name</th>
	 *     <th>Type</th>
	 *     <th>Description</th>
	 *   </tr>
	 *   <tr>
	 *     <td><code>color</code></td>
	 *     <td><code>int</code></td>
	 *     <td>This sets the color index (0-based) of this key.</td>
	 *   </tr>
	 * </table>
	 * @param params the parameter object.
	 */
	@Override
	public void setParameters(Map<String, Object> params)
	{
		super.setParameters(params);
		Object val = params.get("color");
		if (val != null && val instanceof Integer)
		{
			ColorType colors[] = ColorType.values();
			int ind = (Integer)val;
			if (ind >= 0 && ind < colors.length)
				setColor(colors[ind]);
		}
	}

	/**
	 * Checks whether if this item is identical as another. This will
	 * say true if other item is key and has same color.
	 * 
	 * @param other other item to compare with
	 * @return true if both items are identical
	 */
	@Override
	public boolean identical(Item other)
	{
		return other instanceof Key && ((Key)other).getColor() == color;
	}

	/**
	 * Obtains the index that should be displayed as item
	 * @return index of image frame in tileset
	 */
	@Override
	public int getItemImage()
	{
		return color.getImageFrame(KEY_OFFSET_IMAGE);
	}

}
