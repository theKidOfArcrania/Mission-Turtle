package turtle.attributes;

import java.lang.annotation.*;

/**
 * This annotation remarks that a particular method is not an attribute.
 *
 * @author Henry
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface NotAttribute {
    // Marker annotation
}
