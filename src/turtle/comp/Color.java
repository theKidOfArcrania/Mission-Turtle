/**
 * Color.java
 * 
 * This contains an enumeration of the different types of colors
 * that would be associated in the game (currently used by Key
 * and Lock).
 * 
 * @author Henry Wang
 * Period: 2
 * Date: 5/3/17
 */

package turtle.comp;

public enum Color
{
	YELLOW, ORANGE, RED, PURPLE, BLUE, GREEN;
	
	/**
	 * This obtains the index of the image frame that will show this color
	 * based on an offset of the first component (the yellow version).
	 * @param offset the index of the first component (yellow version of it).
	 * @return an image index that will obtain the right color.
	 */
	public int getImageFrame(int offset)
	{
		return ordinal() + offset;
	}
}
