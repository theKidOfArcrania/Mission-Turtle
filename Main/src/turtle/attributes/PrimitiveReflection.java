package turtle.attributes;

/**
 * This utility class helps do primitive and wrapper value bridging.
 *
 * @author Henry Wang
 */
public class PrimitiveReflection {
    private static final boolean DEFAULT_BOOLEAN = false;
    private static final byte DEFAULT_BYTE = 0;
    private static final char DEFAULT_CHAR = 0;
    private static final short DEFAULT_SHORT = 0;
    private static final int DEFAULT_INT = 0;
    private static final long DEFAULT_LONG = 0;
    private static final float DEFAULT_FLOAT = 0;
    private static final double DEFAULT_DOUBLE = 0;

    /**
     * Obtains the primitive version of the wrapper class.
     *
     * @param wrapper the wrapper class to unwrap
     * @return the primitive class (if it exists)
     */
    public static Class<?> getPrimitiveValue(Class<?> wrapper) {
        Object type;
        try {
            type = wrapper.getDeclaredField("TYPE").get(null);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            //This is not a primitive wrapper
            return wrapper;
        }
        if (type instanceof Class) {
            return (Class<?>) type;
        } else {
            return wrapper;
        }
    }

    /**
     * Obtains default value of class (if it is a primitive/wrapper class, it
     * will obtain default value of that type).
     *
     * @param clazz the class to determine default value.
     * @return a default value.
     */
    public static Object getDefaultValue(Class<?> clazz) {
        Class<?> classUse = clazz;

        if (classUse == String.class) {
            return "";
        }
        if (!classUse.isPrimitive()) {
            // Attempt to get primitive type.
            Class<?> prim = getPrimitiveValue(classUse);
            if (prim != null) {
                classUse = prim;
            }
        }
        if (classUse.equals(boolean.class)) {
            return DEFAULT_BOOLEAN;
        } else if (classUse.equals(byte.class)) {
            return DEFAULT_BYTE;
        } else if (classUse.equals(char.class)) {
            return DEFAULT_CHAR;
        } else if (classUse.equals(short.class)) {
            return DEFAULT_SHORT;
        } else if (classUse.equals(int.class)) {
            return DEFAULT_INT;
        } else if (classUse.equals(long.class)) {
            return DEFAULT_LONG;
        } else if (classUse.equals(float.class)) {
            return DEFAULT_FLOAT;
        } else if (classUse.equals(double.class)) {
            return DEFAULT_DOUBLE;
        } else {
            return null;
        }
    }
}
