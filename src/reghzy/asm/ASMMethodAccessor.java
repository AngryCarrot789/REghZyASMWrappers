package reghzy.asm;

import reghzy.asm.utils.ClassGenerator;
import reghzy.asm.utils.ReflectHelper;

import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 * A helper class for creating method accessors that use ASM generated classes to invoke the method
 */
public class ASMMethodAccessor {
    // Generics are used simply for convenience casting, though they are pretty much
    // useless if the return type is primitive... i guess

    /**
     * Generates a method accessor for a method in the given targetClass, with the given return type and parameter types
     * @param targetClass The class in which the method is defined in
     * @param methodName The name of the method
     * @param parameterTypes The method's parameters types
     * @param <T> Target class type (class that the method is defined in)
     * @param <V> Return type
     * @return A method accessor
     */
    public static <T, V> MethodAccessor<T, V> create(Class<T> targetClass, String methodName, Class<?>... parameterTypes) {
        Method method = ReflectHelper.findDeclaredMethod(targetClass, methodName, parameterTypes);
        if (method.getReturnType().isAssignableFrom(method.getReturnType())) {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }

            try {
                Class<?> clazz = ClassGenerator.generate(method);
                return (MethodAccessor<T, V>) clazz.newInstance();
            }
            catch (InstantiationException e) {
                throw new RuntimeException("InstantiationException", e);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("IllegalAccessException", e);
            }
            catch (Throwable e) {
                throw new RuntimeException("Failed to either generate class, or invoke constructor", e);
            }
        }

        throw new RuntimeException(MessageFormat.format("Incompatible method type. Method return type {0} cannot be assigned to {1}",
                                                        method.getReturnType().getName(), method.getReturnType().getName()));
    }

    /**
     * Creates a method accessor around the given method
     * @param method The method to use
     * @param <T> Target class type (class that the method is defined in)
     * @param <V> Return type
     * @return A method accessor
     */
    public static <T, V> MethodAccessor<T, V> create(Method method) {
        if (method.getReturnType().isAssignableFrom(method.getReturnType())) {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }

            try {
                Class<?> clazz = ClassGenerator.generate(method);
                return (MethodAccessor<T, V>) clazz.newInstance();
            }
            catch (InstantiationException e) {
                throw new RuntimeException("InstantiationException", e);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("IllegalAccessException", e);
            }
            catch (Throwable e) {
                throw new RuntimeException("Failed to either generate class, or invoke constructor", e);
            }
        }

        throw new RuntimeException(MessageFormat.format("Incompatible method type. Method return type {0} cannot be assigned to {1}",
                                                        method.getReturnType().getName(), method.getReturnType().getName()));
    }
}
