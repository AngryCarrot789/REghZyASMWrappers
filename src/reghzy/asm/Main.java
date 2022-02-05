package reghzy.asm;

public class Main {
    public static void main(String[] args) {
        // creates the accessor. This instance is purely generated, and the only way to access the source code
        // would be to write the bytes to a file. Look in ClassGenerator on how to do that :)
        MethodAccessor<MyWorld, Integer> accessor = ASMMethodAccessor.create(MyWorld.class, "getBlockId", int.class, int.class, int.class);

        // Must be invokeInt, because that's the only method that gets generated, because why would it return anything else?
        // Invoking "invoke" or "invokeDouble" would throw an AbstractMethodError (thrown by the JVM), because the
        // method was never generated. It wont auto-convert an int to a double, too. But you can just do (double) invokeInt() :)
        int blockId = accessor.invokeInt(new MyWorld(), 5, 10, 15);
        System.out.println(blockId);

        MethodAccessor<MyWorld, Integer> sayHelloAccessor = ASMMethodAccessor.create(MyWorld.class, "sayHello");
        sayHelloAccessor.invokeVoid(new MyWorld());

        MethodAccessor<Main, Double> addNumbersAccessor = ASMMethodAccessor.create(Main.class, "addNumbers", double.class, double.class);
        double value = addNumbersAccessor.invokeDouble(null, 5.0d, 10.0d);
        System.out.println("5.0 + 10.0 = " + value);

        // there is more room to upgrade though. This simply removes the autoboxing/auto-unboxing of the method return types
        // but the parameters are obviously still going to get auto-boxed, due to the object array
        // an update could be to use some sort of "IntHolder" class (with a non-final int field), that asm will use?
        // that would remove the autoboxing of the int parameters...
    }

    public static double addNumbers(double a, double b) {
        return a + b;
    }

    public static class MyWorld {
        public int getBlockId(int x, int y, int z) {
            return x + y + z;
        }

        public void sayHello() {
            System.out.println("hello!!!!");
        }
    }
}
