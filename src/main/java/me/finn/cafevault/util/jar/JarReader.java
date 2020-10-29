package me.finn.cafevault.util.jar;

import me.finn.cafevault.util.CVUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarReader {

    private HashMap<String, byte[]> classMap;
    private HashMap<String, byte[]> resMap;
    private final JarFile jarFile;

    public JarReader(@NotNull JarFile jarFile) {
        this.classMap = new HashMap<>();
        this.resMap = new HashMap<>();
        this.jarFile = jarFile;

        Enumeration<JarEntry> entries = jarFile.entries();

        JarEntry jarEntry;
        String entryName;
        byte[] entryBytes;
        while (entries.hasMoreElements()) {
            jarEntry = entries.nextElement();
            if (jarEntry.isDirectory()) continue;

            entryName = jarEntry.getName();

            try {
                entryBytes = CVUtils.in2ba(jarFile.getInputStream(jarEntry));
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            if (entryName.endsWith(".class")) {
                if (CVUtils.classVerify(entryBytes)) classMap.put(entryName, entryBytes);
                else resMap.put(entryName, entryBytes);

            } else if (entryName.endsWith(".cv"))
                resMap.put(entryName.substring(0, entryName.length() - 3) + ".cvtemp", entryBytes);

            else resMap.put(entryName, entryBytes);
        }
    }

    public void finish() {
        this.classMap = null;
        this.resMap = null;
        try {
            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, byte[]> getClassMap() {
        return classMap;
    }

    public HashMap<String, byte[]> getResMap() {
        return resMap;
    }

    public JarFile getJarFile() {
        return jarFile;
    }
}
