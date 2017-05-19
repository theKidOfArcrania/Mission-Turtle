package turtle.attributes;

/**
 * This interface encompasses any object that contains a set of attributes that can be set.
 *
 * @author Henry
 *
 */
public interface Attributable {

	default Object getAttribute(String name) {
		return getAttributeSet().getAttribute(name);
	}

	default Object getAttribute(String name, Object def) {
		return getAttributeSet().getAttribute(name, def);
	}

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
