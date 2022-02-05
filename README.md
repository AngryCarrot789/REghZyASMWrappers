# REghZyASMWrappers
A small library for creating MethodAccessors, generating using bytecode ASM, which can act as replacements for reflection

## example
```java
// func_72798_a == getBlockId(int,int,int) -> int
MethodAccessor<World, Integer> getIdAccessor = ASMMethodAccessor.create(World.class, "func_72798_a", int.class, int.class, int.class);
World overworld = getWorld("world");
int blockId = getIdAccessor.invokeInt(overworld, 2250, 67, -2434);
System.out.println(blockId);

MethodAccessor<World, Boolean> blockSolidityAccessor = 
        ASMMethodAccessor.create(
                World.class, 
                "isBlockSolidOnSide", 
                int.class, int.class, int.class, ForgeDirection.class);
// returns primitive boolean, not Boolean wrapper
if (blockSolidityAccessor.invokeBool(overworld, 2250, 67, -2434, ForgeDirection.DOWN)) {
    ...
}
```
