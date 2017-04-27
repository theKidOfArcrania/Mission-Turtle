/**
 * CompSpec.java
 * 
 * This represents a grid component specification, which can be used to 
 * directly store into a level data file, or can be used to create a new
 * component with the specs.
 * 
 * @author Henry Wang
 * Date: 4/27/17
 * Period: 2
 */

package turtle.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import turtle.core.Component;
import turtle.core.TileSet;

public class CompSpec
{
	private final TileSet tileset;
	private final int slot;
	private final Map<String, Object> params;
	
	/**
	 * Creates a CompSpec initialized with the component specifications
	 * @param tileset the parent tileset associated with this comp spec.
	 * @param slot the index slot specifying component type
	 * @param params the map associated extra parameters of component.
	 */
	public CompSpec(TileSet tileset, int slot, Map<String, Object> params)
	{
		this.tileset = tileset;
		this.slot = slot;
		this.params = new HashMap<>(params);
	}
	
	/**
	 * Creates a CompSpec initialized with the component specifications
	 * and reads from parameter data
	 * @param tileset the parent tileset associated with this comp spec.
	 * @param slot the index slot specifying component type
	 * @param data the serialized parameter data
	 * @throws IOException if serialized parameter data is corrupted. 
	 */
	@SuppressWarnings("unchecked")
	public CompSpec(TileSet tileset, int slot, byte[] data) throws IOException
	{
		this.tileset = tileset;
		this.slot = slot;
		
		try (ObjectInputStream ois = new ObjectInputStream(
				new ByteArrayInputStream(data)))
		{
			Object o = ois.readObject();
			if (o instanceof Map)
				this.params = new HashMap<>((Map<String, Object>)o);
			else
				throw new IOException("Corrupted params data");
		}
		catch (ClassNotFoundException e)
		{
			throw new IOException(e);
		}
	}
	
	/**
	 * Creates a new component based on the component specifications.
	 * 
	 * @throws IllegalStateException if the component cannot be created
	 * @return a brand new grid component.
	 */
	public Component createComponent()
	{
		Class<Component> clsComp = tileset.componentAt(slot);
		try
		{
			Component c = clsComp.newInstance();
			c.setParameters(params);
			return c;
		} catch (InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
			throw new IllegalStateException("No no-arg constructor found");
		}
		
	}

	/**
	 * @return the component slot index
	 */
	public int getSlot()
	{
		return slot;
	}

	/** 
	 * Sets a parameter value for component
	 * @param name name of parameter
	 * @param value parameter value to set
	 */
	public void setParameter(String name, Object value)
	{
		params.put(name, value);
	}
	
	/**
	 * Gets a parameter value for the component
	 * @param name name of parameter.
	 * @return the current parameter value.
	 */
	public Object getParameter(String name)
	{
		return params.get(name);
	}
	
	/**
	 * Serializes all parameter values into binary data.
	 * @return a byte array of binary data.
	 */
	public byte[] storeParameters() throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(baos))
		{
			oos.writeObject(params);
		}
		return baos.toByteArray();
	}
}
