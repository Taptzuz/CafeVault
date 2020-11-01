package me.finn.cafevault.util;

import me.finn.cafevault.CafeVault;
import me.finn.cafevault.classloader.ByteStreamHandler;
import me.finn.cafevault.classloader.ByteUrlConnection;
import me.finn.cafevault.classloader.CafeClassLoader;
import me.finn.cafevault.cv.CVMain;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;

public class CBPoolUtils {

    private static final HashMap<String, byte[]> classPoolMap = new HashMap<>();

    private static void init() {
        mainNode();
        utilNode();
        bucNode();
        bshNode();
        clNode();
    }

    private static void mainNode() {
        ClassNode classNode = ASMUtils.getNode(CVMain.class);

        //Fetchs Main-Class Field
        classNode.fields.get(2).value = CafeVault.mainClass;

        classPoolMap.put(classNode.name, ASMUtils.getNodeBytes(classNode, true));
    }

    private static void utilNode() {
        ClassNode classNode = ASMUtils.getNode(CVUtils.class);

        classPoolMap.put(classNode.name, ASMUtils.getNodeBytes(classNode, true));
    }

    private static void bucNode() {
        ClassNode classNode = ASMUtils.getNode(ByteUrlConnection.class);

        classPoolMap.put(classNode.name, ASMUtils.getNodeBytes(classNode, true));
    }

    private static void bshNode() {
        ClassNode classNode = ASMUtils.getNode(ByteStreamHandler.class);

        classPoolMap.put(classNode.name, ASMUtils.getNodeBytes(classNode, true));
    }

    private static void clNode() {
        ClassNode classNode = ASMUtils.getNode(CafeClassLoader.class);

        classPoolMap.put(classNode.name, ASMUtils.getNodeBytes(classNode, true));
    }

    public static void refreshList() {
        classPoolMap.clear();
        init();
    }

    public static HashMap<String, byte[]> getClassPoolMap() {
        return classPoolMap;
    }
}
