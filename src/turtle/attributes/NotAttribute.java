package turtle.attributes;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * NotAttribute.java
 *
 * This annotation remarks that a particular method is not an attribute.
 * @author Henry
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface NotAttribute
{
	// Marker annotation
}
