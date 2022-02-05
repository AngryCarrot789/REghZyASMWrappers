package reghzy.asm.utils;

import java.lang.reflect.Method;
import java.text.MessageFormat;

public class ReflectHelper {
    public static Method findDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        if (clazz == null) {
            throw new NullPointerException("Target class cannot be null");
        }

        Class<?> nextClass = clazz;
        while (nextClass != null) {
            try {
                Method method = nextClass.getDeclaredMethod(name, parameterTypes);
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }

                return method;
            }
            catch (NoSuchMethodException e) {
                nextClass = nextClass.getSuperclass();
            }
        }

        throw new RuntimeException(MessageFormat.format("Could not find the declared method '{0}' in the hierarchy for the class '{1}'", name, clazz.getName()));
    }
}
