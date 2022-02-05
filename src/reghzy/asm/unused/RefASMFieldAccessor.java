package reghzy.asm.unused;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.text.MessageFormat;

public class RefASMFieldAccessor {
    // ---------------------------- feel free to read :) ---------------------------------
    // was going to try and do one for fields... but couldn't be bothered
    // ASM can't access private/protected fields because of java's access security
    // and yes... i did try using reflection and Method/Field#setAccessible(true),
    // but that only works for reflection
    // -----------------------------------------------------------------------------------

    private static int NEXT_ID;
    private static final ASMClassLoader LOADER = new ASMClassLoader();
    private static final String ACCESSOR_DESC = Type.getInternalName(FieldAccessor.class);

    public static <T, V> FieldAccessor<T, V> create(Class<T> fieldHolderClass, Class<V> fieldType, String fieldName) {
        try {
            fieldHolderClass.getDeclaredField(fieldName).setAccessible(true);
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to find field: " + fieldHolderClass.getName() + "." + fieldName);
        }

        String HOLDER_DESC = Type.getInternalName(fieldHolderClass);
        String FIELD_DESC = Type.getDescriptor(fieldType);

        String asmClassName = MessageFormat.format("ASMFieldAccessor_{0}_{1}_{2}_{3}", fieldHolderClass.getSimpleName(), fieldType.getSimpleName(), fieldName, NEXT_ID++);
        String asmClassDesc = asmClassName.replace('.', '/');

        ClassWriter cw = new ClassWriter(0);
        cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, asmClassDesc, null, "java/lang/Object", new String[]{ACCESSOR_DESC});
        cw.visitSource(".dynamic", null);

        MethodVisitor ctor = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        ctor.visitCode();
        ctor.visitVarInsn(Opcodes.ALOAD, 0);
        ctor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        ctor.visitInsn(Opcodes.RETURN);
        ctor.visitMaxs(1, 1);
        ctor.visitEnd();

        String GETTER_DESC = Type.getMethodDescriptor(Type.getType(fieldType), Type.getType(fieldHolderClass));
        String SETTER_DESC = Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(fieldHolderClass), Type.getType(fieldType));

        MethodVisitor get = cw.visitMethod(Opcodes.ACC_PUBLIC, "get", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        get.visitCode();
        get.visitVarInsn(Opcodes.ALOAD, 1);
        get.visitTypeInsn(Opcodes.CHECKCAST, HOLDER_DESC);
        // wont work on protected/private fields... smh my head
        get.visitFieldInsn(Opcodes.GETFIELD, HOLDER_DESC, fieldName, FIELD_DESC);
        get.visitInsn(Opcodes.ARETURN);
        get.visitMaxs(1, 2);
        get.visitEnd();

        MethodVisitor set;
        set = cw.visitMethod(Opcodes.ACC_PUBLIC, "set", SETTER_DESC, null, null);
        set.visitCode();
        set.visitVarInsn(Opcodes.ALOAD, 1);
        set.visitVarInsn(Opcodes.ALOAD, 2);
        set.visitFieldInsn(Opcodes.PUTFIELD, HOLDER_DESC, fieldName, FIELD_DESC);
        set.visitInsn(Opcodes.RETURN);
        set.visitMaxs(2, 3);
        set.visitEnd();

        cw.visitEnd();

        try {
            return (FieldAccessor<T, V>) LOADER.define(asmClassName, cw.toByteArray()).newInstance();
        }
        catch (ClassFormatError e) {
            throw new RuntimeException("Malformed class", e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException("Failed to create instance of ASM field accessor", e);
        }
        catch (IllegalAccessException e) {
            throw new Error("Illegal access exception", e);
        }
    }

    private static class ASMClassLoader extends ClassLoader {
        private ASMClassLoader() {
            super(RefASMFieldAccessor.class.getClassLoader());
        }

        public Class<?> define(String name, byte[] data) throws ClassFormatError {
            return defineClass(name, data, 0, data.length);
        }
    }
}
