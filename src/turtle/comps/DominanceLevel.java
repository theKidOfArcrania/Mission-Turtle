/**
 * Represents a dominance level, which determines relative 
 * layer position of the actors and also which actor will
 * dominate the interaction of other actors.
 * 
 * @author Henry Wang
 * Date: 4/26/17
 * Period: 2
 */

package turtle.comps;

import java.io.Serializable;

public class DominanceLevel implements Serializable
{
	private final String name;
	private final int dominanceValue;
	
	/**
	 * Constructs a new DominanceLevel based on a name and level 
	 * of rank of dominance (higher numbers are more dominant).
	 * @param name name of this dominance level (used for better display)
	 * @param dominanceValue
	 */
	public DominanceLevel(String name, int dominanceValue)
	{
		super();
		this.name = name;
		this.dominanceValue = dominanceValue;
	}
	
	/**
	 * @return the name of the dominance level
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * @return the numeric dominance value this object represents
	 */
	public int getDominanceValue()
	{
		return dominanceValue;
	}

	/**
	 * Gets a hash value of this dominance value
	 * @return integer hash value
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + dominanceValue;
		return result;
	}

	/**
	 * Tests whether if two DominanceLevel objects are equal.
	 * @return true if equivalent, false otherwise.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DominanceLevel other = (DominanceLevel) obj;
		if (dominanceValue != other.dominanceValue)
			return false;
		return true;
	}

	/**
	 * @return a string representation of this dominance level
	 */
	@Override
	public String toString()
	{
		return "DominanceLevel [" + name + "] (" + dominanceValue + ")";
	}
	
	
}
