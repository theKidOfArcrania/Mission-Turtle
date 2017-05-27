package turtle.attributes;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * AttributeSet.java
 * <p>
 * Contains a list of key-value pair attributes that describes an attributable object.
 *
 * @param <A> the type of the attributable object
 * @author Henry
 * @see turtle.attributes.Attributable
 */
public class AttributeSet<A extends Attributable>
{

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 6804388197581856495L;
    private static final String GET_PREFIX = "get";
    private static final String IS_PREFIX = "is";
    private static final String SET_PREFIX = "set";
    private static final HashMap<Class<?>, Attribute[]> attributeCache = new
            HashMap<>();
    private final Hashtable<String, Attribute> attrs = new Hashtable<>();
    private final A attributable;

    /**
     * Creates a new attribute set for a particular attributable object.
     * @param obj the attributable object.
     */
    public AttributeSet(A obj)
    {
        attributable = obj;
        if (!attributeCache.containsKey(obj.getClass()))
            extractAttributes(attributable);
        for (Attribute attr : attributeCache.get(obj.getClass()))
            attrs.put(attr.getName(), attr);
    }

    /**
     * Obtains the attribute object of a particular attribute.
     *
     * @param name the name of the attribute.
     * @return an attribute object.
     */
    public Attribute attributeObject(String name)
    {
        return attrs.get(name);
    }

    /**
     * Determines whether if attribute exists.
     *
     * @param name name of the attribute.
     * @return true if exists, false if it doesn't exist.
     */
    public boolean attributeExists(String name)
    {
        return attrs.containsKey(name);
    }

    /**
     * @return a set of possible attributes
     */
    public Set<String> attributes()
    {
        return attrs.keySet();
    }

    /**
     * Checks if method is a valid getter.
     *
     * @param meth getter method to check
     * @return true if this is a valid getter. False otherwise.
     */
    private static boolean checkGetter(Method meth)
    {
        // If it contains the "NotAttribute" annotation, then we
        // never consider it.
        if (meth.getDeclaredAnnotation(NotAttribute.class) != null)
            return false;

        // Must be a non-static non-abstract public no-parameter method
        int mods = meth.getModifiers();
        if (Modifier.isAbstract(mods) || Modifier.isStatic(mods) ||
                !Modifier.isPublic(mods) || meth.getParameterCount() > 0)
            return false;

        //Check if method returns something
        if (meth.getReturnType() == void.class)
            return false;



        //Check if method name starts with "get"/"is", and that there is valid
        //attribute (first letter must be capitalized)
        String getPrefix = meth.getReturnType() == boolean.class ? IS_PREFIX
                : GET_PREFIX;
        String attrName = meth.getName();
        if (!attrName.startsWith(getPrefix))
            return false;

        int offset = getPrefix.length();
        return attrName.length() > offset &&
                Character.isUpperCase(attrName.charAt(offset));
    }

    /**
     * Checks if method is a valid setter.
     *
     * @param meth setter method to check
     * @return true if this is a valid getter. False otherwise.
     */
    private static boolean checkSetter(Method meth)
    {
        // If it contains the "NotAttribute" annotation, then we
        // never consider it.
        if (meth.getDeclaredAnnotation(NotAttribute.class) != null)
            return false;

        // Must be a non-static non-abstract public one-parameter method
        int mods = meth.getModifiers();
        if (Modifier.isAbstract(mods) || Modifier.isStatic(mods) ||
                !Modifier.isPublic(mods) || meth.getParameterCount() != 1)
            return false;

        //Check if method returns something
        if (meth.getReturnType() != void.class)
            return false;

        //Check if method name starts with "set", and that there is valid
        //attribute (first letter must be capitalized)
        String attrName = meth.getName();
        if (!attrName.startsWith(SET_PREFIX))
            return false;

        int offset = SET_PREFIX.length();
        return attrName.length() > offset &&
                Character.isUpperCase(attrName.charAt(offset));
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AttributeSet<?> other = (AttributeSet<?>) obj;
        return attributable.getClass() == other.attributable.getClass() &&
                attrs.equals(other.attrs);
    }

    /**
     * Extracts all the attributes of this attribute type and adds to the
     * current attribute cache.
     * @param attributable the attributable object to extract.
     */
    @SuppressWarnings("unchecked")
    private static void extractAttributes(Object attributable)
    {
        Class<?> type = attributable.getClass();
        ArrayList<Attribute> attrList = new ArrayList<>();
        for (Method getter : type.getMethods())
        {
            // First make sure that this method comes from a class/interface
            // above the Attributable structure
            if (!Attributable.class.isAssignableFrom(getter.getDeclaringClass()))
                continue;

            if (!checkGetter(getter))
                continue;

            String getPrefix = getter.getReturnType() == boolean.class ?
                    IS_PREFIX : GET_PREFIX;
            String attrName = getter.getName().substring(getPrefix.length());

            //Lowercase the first letter to make attribute name
            String setterName = SET_PREFIX + attrName;
            attrName = attrName.substring(0, 1).toLowerCase() +
                    attrName.substring(1);

            //Check if we have a setter method.
            Method setter;
            try
            {
                setter = type.getMethod(setterName, getter.getReturnType());
                if (!checkSetter(setter))
                    setter = null;
            }
            catch (NoSuchMethodException e)
            {
                setter = null;
            }

            Attribute attr = new Attribute(attrName, getter, setter);
            attrList.add(attr);
        }
        attributeCache.put(type, attrList.toArray(new Attribute[0]));
    }

    /**
     * Obtains the attributable type that we are querying attributes from.
     *
     * @return the attributable object.
     */
    public A getAttributable()
    {
        return attributable;
    }

    /**
     * Gets the value of a particular attribute.
     *
     * @param name the name of the attribute.
     * @return the value (or null if attribute does not exist)
     */
    public Object getAttribute(String name)
    {
        return getAttribute(name, null);
    }

    /**
     * Gets the value of a particular attribute.
     *
     * @param name the name of the attribute.
     * @param def  the default value to return.
     * @return the value.
     */
    public Object getAttribute(String name, Object def)
    {
        return attrs.containsKey(name) ? attrs.get(name).get(attributable) : def;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + attributable.getClass().hashCode();
        result = prime * result + attrs.hashCode();
        return result;
    }

    /**
     * Sets the value of a particular attribute.
     *
     * @param name the name of the attribute.
     * @param value the value to set.
     * @throws IllegalArgumentException if value cannot cast into attribute type
     */
    public void setAttribute(String name, Object value)
    {
        if (!attrs.containsKey(name))
            return;
        Attribute attr = attrs.get(name);
        if (!attr.hasSetter())
            return;

        //Check type-casting and also auto-unboxing stuff
        if (!attr.getAttributeType().isInstance(value) &&
                attr.getAttributeType() != PrimitiveReflection
                        .getPrimitiveValue(value.getClass()))
            throw new IllegalArgumentException("Value does not cast to " +
                    "attribute type.");

        attr.set(attributable, value);
    }

}
