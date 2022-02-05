package reghzy.asm.utils;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

// arrays must be an Object[] array, due to how AALOAD works!!! i think
public class ASMHelper {
    public static void loadArrayElementIntoStack(MethodVisitor mv, Class<?> type, int localVarIndex, int arrayIndex) {
        if (type == byte.class) {
            convertAndLoadArrayByteIntoStack(mv, localVarIndex, arrayIndex);
        }
        else if (type == short.class) {
            convertAndLoadArrayShortIntoStack(mv, localVarIndex, arrayIndex);
        }
        else if (type == int.class) {
            convertAndLoadArrayIntIntoStack(mv, localVarIndex, arrayIndex);
        }
        else if (type == long.class) {
            convertAndLoadArrayLongIntoStack(mv, localVarIndex, arrayIndex);
        }
        else if (type == float.class) {
            convertAndLoadArrayFloatIntoStack(mv, localVarIndex, arrayIndex);
        }
        else if (type == double.class) {
            convertAndLoadArrayDoubleIntoStack(mv, localVarIndex, arrayIndex);
        }
        else if (type == boolean.class) {
            convertAndLoadArrayBoolIntoStack(mv, localVarIndex, arrayIndex);
        }
        else if (type == char.class) {
            convertAndLoadArrayCharIntoStack(mv, localVarIndex, arrayIndex);
        }
        else {
            loadArrayElementIntoStack(mv, localVarIndex, arrayIndex);
            mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(type));
        }
    }

    public static void convertAndLoadArrayByteIntoStack(MethodVisitor mv, int localVarIndex, int arrayIndex) {
        loadArrayElementIntoStack(mv, localVarIndex, arrayIndex);
        mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Byte");  // check the ref is actually instanceof Byte
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
    }

    public static void convertAndLoadArrayShortIntoStack(MethodVisitor mv, int localVarIndex, int arrayIndex) {
        loadArrayElementIntoStack(mv, localVarIndex, arrayIndex);
        mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Short");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
    }

    // load array at localVarIndex, push ((Integer)array[arrayIndex]).intValue() into stack
    public static void convertAndLoadArrayIntIntoStack(MethodVisitor mv, int localVarIndex, int arrayIndex) {
        loadArrayElementIntoStack(mv, localVarIndex, arrayIndex);
        mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Integer");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
    }

    // load array at localVarIndex, push ((Long)array[arrayIndex]).longValue() into stack
    public static void convertAndLoadArrayLongIntoStack(MethodVisitor mv, int localVarIndex, int arrayIndex) {
        loadArrayElementIntoStack(mv, localVarIndex, arrayIndex);
        mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Long");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
    }

    public static void convertAndLoadArrayFloatIntoStack(MethodVisitor mv, int localVarIndex, int arrayIndex) {
        loadArrayElementIntoStack(mv, localVarIndex, arrayIndex);
        mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Float");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F");
    }

    public static void convertAndLoadArrayDoubleIntoStack(MethodVisitor mv, int localVarIndex, int arrayIndex) {
        loadArrayElementIntoStack(mv, localVarIndex, arrayIndex);
        mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Double");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
    }

    public static void convertAndLoadArrayBoolIntoStack(MethodVisitor mv, int localVarIndex, int arrayIndex) {
        loadArrayElementIntoStack(mv, localVarIndex, arrayIndex);
        mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Boolean");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
    }

    public static void convertAndLoadArrayCharIntoStack(MethodVisitor mv, int localVarIndex, int arrayIndex) {
        loadArrayElementIntoStack(mv, localVarIndex, arrayIndex);
        mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Character");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C");
    }

    public static void loadArrayElementIntoStack(MethodVisitor mv, int localVarIndex, int arrayIndex) {
        mv.visitVarInsn(Opcodes.ALOAD, localVarIndex);
        loadIntIntoStack(mv, arrayIndex);
        mv.visitInsn(Opcodes.AALOAD);
    }

    public static void loadIntIntoStack(MethodVisitor mv, int arrayIndex) {
        switch (arrayIndex) {
            case 0: mv.visitInsn(Opcodes.ICONST_0); break;
            case 1: mv.visitInsn(Opcodes.ICONST_1); break;
            case 2: mv.visitInsn(Opcodes.ICONST_2); break;
            case 3: mv.visitInsn(Opcodes.ICONST_3); break;
            case 4: mv.visitInsn(Opcodes.ICONST_4); break;
            case 5: mv.visitInsn(Opcodes.ICONST_5); break;
            default: {
                if (arrayIndex < 0) {
                    throw new RuntimeException("Cannot load array index below 0");
                }
                else if (arrayIndex <= Byte.MAX_VALUE) {
                    mv.visitIntInsn(Opcodes.BIPUSH, arrayIndex); // add byte on top of stack
                }
                else if (arrayIndex <= Short.MAX_VALUE) {
                    mv.visitIntInsn(Opcodes.SIPUSH, arrayIndex); // add short on top of stack
                }
                else {
                    // lol index beyond 32767? does java even support 30,000 method parameters?
                    throw new RuntimeException("Cannot load array index beyond " + Short.MAX_VALUE);
                }

                break;
            }
        }
    }
}
