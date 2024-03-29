# REghZyASMWrappers
A small library for creating MethodAccessors, generating using bytecode ASM, which can act as replacements for reflection.

One use i can think of, is Scala-Java interop, because i've found it to be quite dodgy

## Example
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

# What it looks like when decompiled
Take this code:
```java 
MethodAccessor<MyWorld, Integer> accessor = 
    ASMMethodAccessor.create(
            MyWorld.class, 
            "getBlockId", 
            int.class, int.class, int.class);
```

When fully generated, written to a `.class` file, and opened in jd-gui, it will look like this:

```java
import reghzy.asm.Main;
import reghzy.asm.MethodAccessor;

public class REghZyASMMethod_MyWorld_getBlockId_0 implements MethodAccessor {
    public int invokeInt(Object target, Object[] params) {
        return ((Main.MyWorld) target).getBlockId(((Integer) params[0]).intValue(), 
                                                  ((Integer) params[1]).intValue(), 
                                                  ((Integer) params[2]).intValue());
    }
}
```

> I manually renamed the args to `target` and `params`, because they won't keep their name during runtime

# The only big problem
Due to java's access security (private/protected/etc), you can't use the ASM method accessors for invoking inaccessible methods. So basically, you can only really invoke public methods, and i suppose you could invoke package-private/protected methods if you edit the ASM generated class' package location, but i haven't tried that

So unless it's possible to somehow add a bypass, i can only see this ASM method accessor being useful for dynamically loaded classes, e.g minecraft/bukkit plugins (event handlers maybe), invoking methods where you don't have access to the actual source code (e.g missing a library reference in your intellij project), etc

There's also a few problems i assume one might run into, and it's to do with NoClassDefErrors being thrown. Classes are defined not only by FQCN (fully qualified class name; class name and package), but also their classloader. So if the ASM tries to invoke a method in a class that was loaded by a classloader that isn't similar to the ASMClassLoader, then the class won't be found.

I'm not sure if making the ASMClassLoader's parent equal to the parent of the target method's classloader will fix it. E.g:

`ASMClassLoader -> LaunchClassLoader -> SystemClassLoader -> null`

`BukkitPluginClassLoader -> LaunchClassLoader -> SystemClassLoader -> null`

But i remember testing this with minecraft forge, and this didn't work (i was trying to listen to forge events from a bukkit plugin. Plugins are loaded by a PluginClassLoader, whose parents are LaunchClassLoader, but forge events are loaded by LaunchClassLoader, and the ASM event handlers are loaded by an ASMClassLoader whose parent is LaunchClassLoader)

I also have no idea why i kept writing what i wrote above... but if you read it all and understand some of it... nice :)
