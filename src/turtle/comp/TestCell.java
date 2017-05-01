package turtle.comp;

import turtle.core.Actor;
import turtle.core.Cell;

public class TestCell extends Cell
{
	
	public TestCell()
	{
		animateFrames(new int[] {12,13,14,15,16,17}, true);
		System.out.println("HELLO");
	}
	
	@Override
	public boolean pass(Actor visitor)
	{
		return true;
	}
	
}
