package me.finn.cafevault.cv;

import me.finn.cafevault.classloader.CafeClassLoader;
import me.finn.cafevault.util.CVUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static me.finn.cafevault.util.CVUtils.*;

public class CVMain {

    private static HashMap<String, byte[]> classMap, resMap;
    private static String mainClass;

    public static void main(String[] args) {
        try {
            JarFile jarFile = new JarFile(new File(CVMain.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("file:", "")));

            classMap = new HashMap<>();
            resMap = new HashMap<>();

            Enumeration<JarEntry> entries = jarFile.entries();

            JarEntry jarEntry;
            String entryName;
            byte[] entryBytes;

            while (entries.hasMoreElements()) {
                jarEntry = entries.nextElement();
                entryName = jarEntry.getName();
                entryBytes = CVUtils.in2ba(jarFile.getInputStream(jarEntry));

                if (!entryName.endsWith(".cv")) {
                    if (entryName.endsWith(".cvtemp"))
                        entryName = formatCVTempToRes(entryName);
                    CVUtils.xor(entryBytes, /*Integer.MAX_VALUE*/ 8);
                    resMap.put(entryName, entryBytes);
                    continue;
                } else
                    entryName = xor(entryName.substring(0, entryName.length() - 3), 7);
                if (entryName.endsWith(".class")) {
                    if (!CVUtils.classVerify(entryBytes)) {
                        resMap.put(entryName, entryBytes);
                        continue;
                    }
                    entryName = formatEntryClassName(entryName);
                    System.out.println(entryName);
                    classMap.put(entryName, entryBytes);
                    continue;
                }

                entryName = formatCVToClassName(entryName);
                entryName = formatEntryClassName(entryName);

                //decrypt
                CVUtils.xor(entryBytes, /*Integer.MAX_VALUE*/ 8);
                classMap.put(entryName, entryBytes);
            }

            CafeClassLoader cafeClassLoader = new CafeClassLoader(classMap, resMap);
            try {
                Class<?> clazz = cafeClassLoader.loadClass(mainClass);
                Method main = clazz.getDeclaredMethod("main", String[].class);

                System.gc();

                Object[] argWrap = new Object[]{new String[]{}};
                main.invoke(null, argWrap);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
