package turtle.comp;

import turtle.core.Actor;
import turtle.core.Cell;

public class TestCell extends Cell
{
	private static final int SHUFFLE = 50;
	public TestCell()
	{
		int[] arr = {18,19,20};
		for (int i = 0; i < SHUFFLE; i++)
		{
			int a = (int)(Math.random() * arr.length);
			int b = (int)(Math.random() * arr.length);
			int tmp = arr[a];
			arr[a] = arr[b];
			arr[b] = tmp;
		}
		animateFrames(arr, true, 4);
	}
	
	@Override
	public boolean pass(Actor visitor)
	{
		return true;
	}
	
}
