/**
 * Represents a location of a Component. 
 */
package turtle.comps;

import java.io.Serializable;

public class Location implements Serializable
{
	private int row;
	private int col;
	
	/**
	 * Constructs an invalid location
	 */
	public Location()
	{
		this(-1, -1);
	}
	
	/**
	 * Constructs a location at the following row and column.
	 * @param row initial row value
	 * @param col initial column value
	 */
	public Location(int row, int col)
	{
		this.row = row;
		this.col = col;
	}

	/**
	 * Copy constructor that copies from the other location.
	 * @param other location to clone from.
	 */
	public Location(Location other)
	{
		this.row = other.row;
		this.col = other.col;
	}
	
	/**
	 * Determines whether if this location is valid or not.
	 * @return true for valid locations, false for invalid locations.
	 */
	public boolean isValidLocation()
	{
		return row >= 0 && col >= 0;
	}

	/**
	 * @return the current row
	 */
	public int getRow()
	{
		return row;
	}

	/**
	 * @param row the row to set to
	 */
	public void setRow(int row)
	{
		this.row = row;
	}

	/**
	 * @return the current column
	 */
	public int getColumn()
	{
		return col;
	}

	/**
	 * @param col the column to set to
	 */
	public void setColumn(int col)
	{
		this.col = col;
	}

	/**
	 * Set this location to the values of the other location.
	 * @param other location values to set to.
	 */
	public void setLocation(Location other)
	{
		this.row = other.row;
		this.col = other.col;
	}
	
	/**
	 * @return a hashcode generated from the current position
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + col;
		result = prime * result + row;
		return result;
	}

	/**
	 * Tests if this location and other location objects are the same.
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
		Location other = (Location) obj;
		if (!other.isValidLocation() && !isValidLocation())
			return true;
		if (col != other.col)
			return false;
		if (row != other.row)
			return false;
		return true;
	}
	
	
}
