package me.finn.cafevault.util;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;

public class ASMUtils {

    public static @NotNull ClassNode getNode(Object obj) {
        ClassReader classReader;
        if (obj instanceof byte[])
            classReader = new ClassReader((byte[]) obj);
        else {
            try {
                classReader = new ClassReader(((Class<?>) obj).getName());
            } catch (IOException e) {
                e.printStackTrace();
                return new ClassNode();
            }
        }

        ClassNode classNode = new ClassNode();
        try {
            classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
        } catch (Exception e) {
            classReader.accept(classNode, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
        }
        return classNode;
    }

    public static byte[] getNodeBytes(@NotNull ClassNode classNode, boolean useMaxs) {
        ClassWriter classWriter = new ClassWriter(useMaxs ? ClassWriter.COMPUTE_MAXS : ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

}
