package turtle.attributes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Attribute.java
 *
 * Holds the getters/setters of a particular attribute.
 */
public class Attribute
{
    private final String name;
    private final Method getter;
    private final Method setter;

    /**
     * Creates an attribute object.
     * @param name the name of attribute.
     * @param getter the method to get value. Must be non-null.
     * @param setter the method to set value. Can be null.
     * @throws NullPointerException if a parameter is null.
     * @throws IllegalArgumentException if the getter/setter method is invalid.
     */
    Attribute(String name, Method getter, Method setter)
    {
        Objects.requireNonNull(name, "Name must be non-null.");
        Objects.requireNonNull(getter, "Getter must be non-null.");
        this.name = name;
        this.getter = getter;
        this.setter = setter;

        if (setter != null)
        {
            if (setter.getParameterCount() != 1 ||
                    getter.getReturnType() != setter.getParameterTypes()[0])
                throw new IllegalArgumentException("Illegal getters/setters.");
        }
    }

    /**
     * @return the name of the attribute.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return true if a setter exists.
     */
    public boolean hasSetter()
    {
        return setter != null;
    }

    /**
     * @return the class type of this attribute.
     */
    public Class<?> getAttributeType()
    {
        return getter.getReturnType();
    }

    /**
     * Invokes the getter of this attribute.
     * @param object the object containing attribute
     * @return the value of attribute.
     * @throws RuntimeException if an error occurs while invoking getter
     */
    public Object get(Attributable object)
    {
        try
        {
            return getter.invoke(object);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            throw new Error("Getter method is not public.");
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e.getCause());
        }
    }

    /**
     * Invokes the setter of this attribute.
     * @param object the object containing attribute
     * @param value the new value of attribute.
     * @throws IllegalStateException if a setter is not available.
     * @throws RuntimeException if an error occurs while invoking setter
     */
    public void set(Attributable object, Object value)
    {
        if (setter == null)
            throw new IllegalStateException("No setter available");

        try
        {
            setter.invoke(object, value);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            throw new Error("Setter method is not public.");
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e.getCause());
        }
    }
}
