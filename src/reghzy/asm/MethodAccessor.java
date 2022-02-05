package reghzy.asm;

/**
 * A class which has access to invoking methods
 * @param <T> The type of class in which the method is defined in (e.g ArrayList, for size())
 * @param <V> The method's return type (e.g {@link Integer} for size(), or null for void return types)
 */
public interface MethodAccessor<T, V> {
    V invoke(T target, Object... params);

    byte invokeByte(T target, Object... params);

    short invokeShort(T target, Object... params);

    int invokeInt(T target, Object... params);

    long invokeLong(T target, Object... params);

    float invokeFloat(T target, Object... params);

    double invokeDouble(T target, Object... params);

    boolean invokeBool(T target, Object... params);

    char invokeChar(T target, Object... params);

    void invokeVoid(T target, Object... params);
}
