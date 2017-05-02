package turtle.comp;

import turtle.core.Actor;
import turtle.core.Cell;

public class TestCell extends Cell
{
	private static final int SHUFFLE = 50;
	public TestCell()
	{
		int[] arr = {35,36,37,38};
//		for (int i = 0; i < SHUFFLE; i++)
//		{
//			int a = (int)(Math.random() * arr.length);
//			int b = (int)(Math.random() * arr.length);
//			int tmp = arr[a];
//			arr[a] = arr[b];
//			arr[b] = tmp;
//		}
		animateFrames(arr, true);
		
		//setRotate((int)(Math.random() * 4) * 90);
		//TODO: have set heading in Actor.
	}
	
	@Override
	public boolean pass(Actor visitor)
	{
		return true;
	}
	
}
