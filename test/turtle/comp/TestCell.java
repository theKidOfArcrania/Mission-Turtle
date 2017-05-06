package turtle.comp;

import turtle.core.Actor;
import turtle.core.Cell;

/**
 * TestCell.java
 * 
 * This is used to test a number of different functions of a cell.
 * 
 * @author Henry
 * Date: 5/6/17
 * Period: 2
 */
public class TestCell extends Cell
{
	public static final int DEFAULT_IMAGE = 51;
	
	/**
	 * Constructs a new TestCell.
	 */
	public TestCell()
	{
//		int[] arr = {18,19,20};
//		for (int i = 0; i < SHUFFLE; i++)
//		{
//			int a = (int)(Math.random() * arr.length);
//			int b = (int)(Math.random() * arr.length);
//			int tmp = arr[a];
//			arr[a] = arr[b];
//			arr[b] = tmp;
//		}
//		animateFrames(arr, true);
		
		setImageFrame(51);
		
		//setRotate((int)(Math.random() * 4) * 90);
	}
	
	/**
	 * Allows everything to pass cell, and logs pass.
	 * @param visitor actor that visited.
	 * @return true always to allow pass.
	 */
	@Override
	public boolean pass(Actor visitor)
	{
		return true;
	}
	
}
