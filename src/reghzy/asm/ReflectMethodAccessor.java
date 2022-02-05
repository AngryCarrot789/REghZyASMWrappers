package reghzy.asm;

import reghzy.asm.utils.ReflectHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 * A method accessor that uses reflection to invoke methods
 * @param <T> The type of class in which the method is defined in (e.g ArrayList, for size())
 * @param <V> The method's return type (e.g {@link Integer} for size(), or null for void return types)
 */
public class ReflectMethodAccessor<T, V> implements MethodAccessor<T, V> {
    private static final Object[] EMPTY_ARR = new Object[0];

    private final Class<T> ownerClass;
    private final Class<V> returnType;
    private final String methodName;
    private final Method method;

    /**
     * Crates a reflect field accessor that uses the given field
     */
    public ReflectMethodAccessor(Method method) {
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }

        this.method = method;
        this.ownerClass = (Class<T>) method.getDeclaringClass();
        this.returnType = (Class<V>) method.getReturnType();
        this.methodName = method.getName();
    }

    private ReflectMethodAccessor(Class<T> owner, Method method, String fieldName) {
        this.ownerClass = owner;
        this.method = method;
        this.returnType = (Class<V>) method.getReturnType();
        this.methodName = fieldName;
    }

    /**
     * Creates a field accessor that uses reflection to get/set the field
     * @param targetClass The class in which the given field is stored in (can be a derived class, where the field is stored in a super class)
     * @param returnType   The class/type of the field. This will be checked against the actual field in the given target class
     * @param fieldName   Name of the field
     * @param <T>         Target class type
     * @param <V>         Field type
     * @return A field accessor
     */
    public static <T, V> ReflectMethodAccessor<T, V> create(Class<T> targetClass, String fieldName, Class<V> returnType, Class<?>... parameterTypes) {
        Method method = ReflectHelper.findDeclaredMethod(targetClass, fieldName, parameterTypes);
        if (returnType.isAssignableFrom(method.getReturnType())) {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }

            return new ReflectMethodAccessor<T, V>(targetClass, method, fieldName);
        }

        throw new RuntimeException(MessageFormat.format("Incompatible method type. Method return type {0} cannot be assigned to {1}", method.getReturnType().getName(), returnType.getName()));
    }

    /**
     * Creates a field accessor that uses reflection to get/set the field
     * <p>
     * This bypasses the field type checks that {@link ReflectMethodAccessor#create(Class, String, Class, Class[])}
     * does, therefore assuming the correct type is always passed
     * </p>
     * @param targetClass The class in which the given field is stored in (can be a derived class, where the field is stored in a super class)
     * @param fieldName   Name of the field
     * @param <T>         Target class type
     * @param <V>         Field type
     * @return A field accessor
     */
    public static <T, V> ReflectMethodAccessor<T, V> create(Class<T> targetClass, String fieldName, Class<?>... parameterTypes) {
        Method method = ReflectHelper.findDeclaredMethod(targetClass, fieldName, parameterTypes);
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }

        return new ReflectMethodAccessor<T, V>(targetClass, method, fieldName);
    }

    public Class<T> getOwnerClass() {
        return this.ownerClass;
    }

    public Class<V> getReturnType() {
        return this.returnType;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public Method getMethod() {
        return this.method;
    }

    @Override
    public V invoke(T target, Object... params) {
        try {
            return (V) this.method.invoke(target, params);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Method access was externally changed; IllegalAccessException", e);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException("Unhandled exception while invoking method", e);
        }
    }

    // all of this auto-unboxing required... not nice!

    @Override
    public byte invokeByte(T target, Object... params) {
        return (Byte) invoke(target, params);
        // this is what this actually looks like, beyond intellij syntax sugar i guess:
        // ((Byte) invoke(target, params)).byteValue()
    }

    @Override
    public short invokeShort(T target, Object... params) {
        return (Short) invoke(target, params);
    }

    @Override
    public int invokeInt(T target, Object... params) {
        return (Integer) invoke(target, params);
    }

    @Override
    public long invokeLong(T target, Object... params) {
        return (Long) invoke(target, params);
    }

    @Override
    public float invokeFloat(T target, Object... params) {
        return (Float) invoke(target, params);
    }

    @Override
    public double invokeDouble(T target, Object... params) {
        return (Double) invoke(target, params);
    }

    @Override
    public boolean invokeBool(T target, Object... params) {
        return (Boolean) invoke(target, params);
    }

    @Override
    public char invokeChar(T target, Object... params) {
        return (Character) invoke(target, params);
    }

    @Override
    public void invokeVoid(T target, Object... params) {
        try {
            this.method.invoke(target, params);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Method access was externally changed; IllegalAccessException", e);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException("Unhandled exception while invoking method", e);
        }
    }

    @Override
    public V invoke(T target) {
        try {
            return (V) this.method.invoke(target, EMPTY_ARR);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Method access was externally changed; IllegalAccessException", e);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException("Unhandled exception while invoking method", e);
        }
    }

    // all of this auto-unboxing required... not nice!

    @Override
    public byte invokeByte(T target) {
        return (Byte) invoke(target);
    }

    @Override
    public short invokeShort(T target) {
        return (Short) invoke(target);
    }

    @Override
    public int invokeInt(T target) {
        return (Integer) invoke(target);
    }

    @Override
    public long invokeLong(T target) {
        return (Long) invoke(target);
    }

    @Override
    public float invokeFloat(T target) {
        return (Float) invoke(target);
    }

    @Override
    public double invokeDouble(T target) {
        return (Double) invoke(target);
    }

    @Override
    public boolean invokeBool(T target) {
        return (Boolean) invoke(target);
    }

    @Override
    public char invokeChar(T target) {
        return (Character) invoke(target);
    }

    @Override
    public void invokeVoid(T target) {
        try {
            this.method.invoke(target, EMPTY_ARR);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Method access was externally changed; IllegalAccessException", e);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException("Unhandled exception while invoking method", e);
        }
    }
}
