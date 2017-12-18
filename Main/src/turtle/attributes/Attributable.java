package turtle.attributes;

import java.io.Serializable;

/**
 * This interface encompasses any object that contains a set of attributes that can be set.
 *
 * @author Henry
 */
public interface Attributable extends Serializable {

    /**
     * Gets the value of a particular attribute.
     *
     * @param name the name of the attribute.
     * @return the value (or null if attribute does not exist)
     * @see AttributeSet#getAttribute(String)
     */
    default Object getAttribute(String name) {
        return getAttributeSet().getAttribute(name);
    }

    /**
     * Gets the value of a particular attribute.
     *
     * @param name the name of the attribute.
     * @param def  the default value to return.
     * @return the value.
     * @see AttributeSet#getAttribute(String, Object)
     */
    default Object getAttribute(String name, Object def) {
        return getAttributeSet().getAttribute(name, def);
    }

    /**
     * Sets the value of a particular attribute.
     *
     * @param name  the name of the attribute.
     * @param value the value to set.
     * @throws IllegalArgumentException if value cannot cast into attribute type
     * @see AttributeSet#setAttribute(String, Object)
     */
    default void setAttribute(String name, Object value) {
        getAttributeSet().setAttribute(name, value);
    }

    /**
     * Returns the attribute set that describes this attributable object.
     *
     * @return the attribute set
     */
    AttributeSet<?> getAttributeSet();

}
