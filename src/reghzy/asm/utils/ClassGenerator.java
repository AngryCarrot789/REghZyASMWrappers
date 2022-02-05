package reghzy.asm.utils;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import reghzy.asm.ASMMethodAccessor;
import reghzy.asm.MethodAccessor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;

/**
 * A class for generating classes... duh lol
 */
public class ClassGenerator {
    private static final ASMClassLoader CLASSLOADER = new ASMClassLoader();
    private static final String ACCESSOR_DESC = Type.getInternalName(MethodAccessor.class); // reghzy/asm/MethodAccessor
    private static final String INVOKE_REF = "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;";
    private static final String INVOKE_BYTE = "(Ljava/lang/Object;[Ljava/lang/Object;)B";
    private static final String INVOKE_SHORT = "(Ljava/lang/Object;[Ljava/lang/Object;)S";
    private static final String INVOKE_INT = "(Ljava/lang/Object;[Ljava/lang/Object;)I";
    private static final String INVOKE_LONG = "(Ljava/lang/Object;[Ljava/lang/Object;)J";
    private static final String INVOKE_FLOAT = "(Ljava/lang/Object;[Ljava/lang/Object;)F";
    private static final String INVOKE_DOUBLE = "(Ljava/lang/Object;[Ljava/lang/Object;)D";
    private static final String INVOKE_BOOL = "(Ljava/lang/Object;[Ljava/lang/Object;)Z";
    private static final String INVOKE_CHAR = "(Ljava/lang/Object;[Ljava/lang/Object;)C";
    private static final String INVOKE_VOID = "(Ljava/lang/Object;[Ljava/lang/Object;)V";
    private static final String INVOKE_REF_0P = "(Ljava/lang/Object;)Ljava/lang/Object;";
    private static final String INVOKE_BYTE_0P = "(Ljava/lang/Object;)B";
    private static final String INVOKE_SHORT_0P = "(Ljava/lang/Object;)S";
    private static final String INVOKE_INT_0P = "(Ljava/lang/Object;)I";
    private static final String INVOKE_LONG_0P = "(Ljava/lang/Object;)J";
    private static final String INVOKE_FLOAT_0P = "(Ljava/lang/Object;)F";
    private static final String INVOKE_DOUBLE_0P = "(Ljava/lang/Object;)D";
    private static final String INVOKE_BOOL_0P = "(Ljava/lang/Object;)Z";
    private static final String INVOKE_CHAR_0P = "(Ljava/lang/Object;)C";
    private static final String INVOKE_VOID_0P = "(Ljava/lang/Object;)V";
    private static int NEXT_ID = 0;

    /**
     * Generates and loads a class, that inherits {@link MethodAccessor}, for the given method
     * <p>
     *     This will only generate the specific method that corresponds to the given method's return type.
     *     If the method returns int, then invoking {@link MethodAccessor#invoke(Object, Object...)} will
     *     throw an {@link AbstractMethodError}. Therefore, you can only invoke {@link MethodAccessor#invokeInt(Object, Object...)}
     * </p>
     * @param method The method to create a wrapper around
     * @return The class type that inherits {@link MethodAccessor}
     * @see Class#newInstance()
     */
    public static Class<?> generate(Method method) {
        // generates a unique class name, not in a package or anything
        String className = MessageFormat.format("REghZyASMMethod_{0}_{1}_{2}", method.getDeclaringClass().getSimpleName(), method.getName(), NEXT_ID++);
        ClassWriter cw = createClassAndCtor(className);
        createInvokeMethod(cw, method, method.getReturnType(), method.getParameterTypes().length == 0);
        cw.visitEnd();

        // -----------------------------------------------------------
        // if you wanna see the code of the generated class,
        // you can use this code:
        //
        // File file = new File(className + ".class");
        // try {
        //     file.createNewFile();
        //     BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(className + ".class"));
        //     out.write(cw.toByteArray()); // yes... you can just write the raw bytes :)
        //     out.close();
        // }
        // catch (Throwable e) {
        //     e.printStackTrace();
        // }
        //
        // Then, just use a decompiler of your choice, e.g jd-gui,
        // or intellij's own decompiler (fernflower?), but
        // jd-gui is more "verbose" in the code. Intellij won't show
        // Integer#intValue() if a parameter is int
        // -----------------------------------------------------------

        // define/register the class' bytes with java
        return CLASSLOADER.define(className, cw.toByteArray());
    }

    private static ClassWriter createClassAndCtor(String classDescriptor) {
        ClassWriter cw = new ClassWriter(0);

        // source file name. i got .dynamic from minecraft forge's ASMEventHandler...
        // which is what got me into ASM... fun fact about me :-)
        cw.visitSource(".dynamic", null);
        cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, classDescriptor, null, "java/lang/Object", new String[]{ACCESSOR_DESC});

        // generate constructor
        MethodVisitor ctor = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        ctor.visitCode();
        ctor.visitVarInsn(Opcodes.ALOAD, 0);
        ctor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        ctor.visitInsn(Opcodes.RETURN);

        // stack: this
        // locals: this
        ctor.visitMaxs(1, 1);
        ctor.visitEnd();

        return cw;
    }

    private static void createInvokeMethod(ClassVisitor cw, Method method, Class<?> returnType, boolean useEmptyParamDescriptor) {
        if (returnType.isPrimitive()) {
            createInvokerPrimitive(cw, method, returnType, useEmptyParamDescriptor);
        }
        else {
            createInvoker(cw, method,
                          "invoke",
                          useEmptyParamDescriptor ? INVOKE_REF_0P : INVOKE_REF,
                          Opcodes.ARETURN);
        }
    }

    private static void createInvokerPrimitive(ClassVisitor cw, Method method, Class<?> primitive, boolean useEmptyParamDescriptor) {
        String invokeName;
        String invokeDesc;
        int returnOpCode;
        if (primitive == byte.class) {
            invokeName = "invokeByte";
            invokeDesc = useEmptyParamDescriptor ? INVOKE_BYTE_0P : INVOKE_BYTE;
            returnOpCode = Opcodes.IRETURN;
        }
        else if (primitive == short.class) {
            invokeName = "invokeShort";
            invokeDesc = useEmptyParamDescriptor ? INVOKE_SHORT_0P : INVOKE_SHORT;
            returnOpCode = Opcodes.IRETURN;
        }
        else if (primitive == int.class) {
            invokeName = "invokeInt";
            invokeDesc = useEmptyParamDescriptor ? INVOKE_INT_0P : INVOKE_INT;
            returnOpCode = Opcodes.IRETURN;
        }
        else if (primitive == long.class) {
            invokeName = "invokeLong";
            invokeDesc = useEmptyParamDescriptor ? INVOKE_LONG_0P : INVOKE_LONG;
            returnOpCode = Opcodes.LRETURN;
        }
        else if (primitive == float.class) {
            invokeName = "invokeFloat";
            invokeDesc = useEmptyParamDescriptor ? INVOKE_FLOAT_0P : INVOKE_FLOAT;
            returnOpCode = Opcodes.FRETURN;
        }
        else if (primitive == double.class) {
            invokeName = "invokeDouble";
            invokeDesc = useEmptyParamDescriptor ? INVOKE_DOUBLE_0P : INVOKE_DOUBLE;
            returnOpCode = Opcodes.DRETURN;
        }
        else if (primitive == boolean.class) {
            invokeName = "invokeBool";
            invokeDesc = useEmptyParamDescriptor ? INVOKE_BOOL_0P : INVOKE_BOOL;
            returnOpCode = Opcodes.IRETURN;
        }
        else if (primitive == char.class) {
            invokeName = "invokeChar";
            invokeDesc = useEmptyParamDescriptor ? INVOKE_CHAR_0P : INVOKE_CHAR;
            returnOpCode = Opcodes.IRETURN;
        }
        else {
            invokeName = "invokeVoid";
            invokeDesc = useEmptyParamDescriptor ? INVOKE_VOID_0P : INVOKE_VOID;
            returnOpCode = Opcodes.RETURN;
        }

        createInvoker(cw, method, invokeName, invokeDesc, returnOpCode);
    }

    // creates a specific invoke method
    private static void createInvoker(ClassVisitor cw, Method method, String invokeName, String invokeDescriptor, int returnOpCode) {
        // don't care about generic details in the methods; they aren't necessary
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, invokeName, invokeDescriptor, null, null);
        mv.visitCode();

        // Removes unnecessary loading and casting of a most likely
        // null target, that also won't get used for static methods
        // if github uploaded the .class, you may see in "REghZyASMMethod_Main_addNumbers_2", it loads an instance of Main
        // as a local variable, but doesn't use it. That's what happens if you don't check for a static method
        // it won't crash or throw an exception, it will just slow your program down by maybe 10 microseconds
        if (!Modifier.isStatic(method.getModifiers())) {
            mv.visitVarInsn(Opcodes.ALOAD, 1);  // load first argument
            mv.visitTypeInsn(Opcodes.CHECKCAST, // cast the value from Object to whatever
                             Type.getInternalName(method.getDeclaringClass()));
        }

        // load parameters into stack, auto-unboxing primitive wrappers back to primitives
        Class<?>[] methodParams = method.getParameterTypes();
        for (int i = 0; i < methodParams.length; i++) {
            ASMHelper.loadArrayElementIntoStack(mv, methodParams[i], 2, i);
        }

        // example: String#toString(), becomes:
        // INVOKEVIRTUAL java/lang/String toString ()Ljava/lang/String;
        // or another: MyClass#helloTher(Integer.valueOf(25)), could become:
        // INVOKEVIRTUAL reghzy/test/MyClass helloTher (Ljava/lang/Integer;)V;
        // invoke the method itself

        mv.visitMethodInsn(getInvocationOpcode(method), Type.getInternalName(method.getDeclaringClass()), method.getName(), Type.getMethodDescriptor(method));
        mv.visitInsn(returnOpCode);

        // stack = this + target_ref + parameters
        // locals = this, target, param_array
        mv.visitMaxs(2 + calculateStackSize(methodParams), 3);
        mv.visitEnd();
    }

    private static int getInvocationOpcode(Method method) {
        int mods = method.getModifiers();
        if (Modifier.isStatic(mods)) {
            return Opcodes.INVOKESTATIC;
        }
        // else if (Modifier.isInterface(mods)) { ??? could use maybe...
        //     return Opcodes.INVOKEINTERFACE;
        // }
        else {
            return Opcodes.INVOKEVIRTUAL;
        }
    }

    private static int calculateStackSize(Method method) {
        return calculateStackSize(method.getParameterTypes());
    }

    private static int calculateStackSize(Class<?>[] parameters) {
        int extra = 0;
        for(Class<?> parameter : parameters) {
            // because java, double and long take up 2 slots in the operand stack
            if (parameter == double.class || parameter == long.class) {
                extra++;
            }
        }

        return parameters.length + extra;
    }

    private static class ASMClassLoader extends ClassLoader {
        private ASMClassLoader() {
            super(ASMMethodAccessor.class.getClassLoader());
        }

        public Class<?> define(String name, byte[] data) throws ClassFormatError {
            // does not use a protection domain
            return defineClass(name, data, 0, data.length);
        }
    }
}
