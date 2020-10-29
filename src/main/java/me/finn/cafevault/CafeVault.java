package me.finn.cafevault;

import me.finn.cafevault.cv.CVMain;
import me.finn.cafevault.util.CBPoolUtils;
import me.finn.cafevault.util.CVUtils;
import me.finn.cafevault.util.RandomUtil;
import me.finn.cafevault.util.jar.JarReader;
import me.finn.cafevault.util.jar.JarWriter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class CafeVault {

    public static File outFile = new File(System.getProperty("user.home") + "/Desktop/" + "CV_Output.jar");

    private static JarReader jarReader;
    private static JarWriter jarWriter;
    private static HashMap<String, String> classNameMap;

    public static final boolean DELETE_FILE_ON_EXIT = false;
    private static final boolean EXECUTE_AFTER_BUILD = true;

    public static void main(String[] args) {
        try {
            classNameMap = new HashMap<>();
            jarReader = new JarReader(new JarFile(new File("./Test.jar")));
            if (outFile.exists()) if (outFile.delete()) System.out.println("Deleted Existing File");

            if (DELETE_FILE_ON_EXIT) outFile.deleteOnExit();

            CafeVault.jarWriter = new JarWriter(outFile);

            long before = System.currentTimeMillis();

            jarReader.getClassMap().forEach((s, bytes) -> {
                try {
                    //encrypt bytes via xor (at moment)
                    String className = CVUtils.xor(CVUtils.formatClassToCVName(s), 7) + ".cv";
                    byte[] encClassBytes = CVUtils.xor(bytes, /*Integer.MAX_VALUE*/ 8);

                    jarWriter.write(className, encClassBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            AtomicBoolean foundManifest = new AtomicBoolean(false);
            String manifest = "META-INF/MANIFEST.MF";
            jarReader.getResMap().forEach((s, bytes) -> {
                try {
                    //encrypt bytes via xor (at moment)
                    //TODO: add res-name enc option
                    if (s.equals(manifest)) {
                        foundManifest.set(true);
                        return;
                    }
                    byte[] encResBytes = CVUtils.xor(bytes,/*Integer.MAX_VALUE*/ 8);
                    jarWriter.write(s, encResBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            CBPoolUtils.getClassPoolMap().forEach((s, bytes) -> {
                ClassReader classReader = new ClassReader(bytes);
                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

                ClassRemapper visitor = new ClassRemapper(classWriter, new Remapper() {
                    @Override
                    public String map(String internalName) {
                        if (CBPoolUtils.getClassPoolMap().containsKey(getCVClassName(internalName, false)))
                            return super.map(getCVClassName(internalName, true));
                        return super.map(internalName);
                    }
                });

                classReader.accept(visitor, 0);
                try {
                    jarWriter.write(getCVClassName(classReader.getClassName(), true) + ".class", classWriter.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            //Add Edited/New Manifest
            jarWriter.write(manifest, getManifest(foundManifest.get() ? jarReader.getJarFile().getInputStream(jarReader.getJarFile().getEntry(manifest)) : null, getCVClassName(CVUtils.formatCVEntryName(CVMain.class.getName()), true)));
            jarReader.finish();
            jarWriter.finish();

            long after = System.currentTimeMillis();

            System.out.println("Finished, Time Elapsed: " + (after - before) + "ms.");
            System.out.println("Executing Jar...");

            if (EXECUTE_AFTER_BUILD) {
                ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", outFile.getAbsolutePath());
                processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
                Process process = processBuilder.start();
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }

                process.destroyForcibly();
                if (DELETE_FILE_ON_EXIT && outFile.delete()) System.out.println("File deleted");
            }
            System.exit(0);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    public static String getCVClassName(String className, boolean fetchNewName) {
        if (!classNameMap.containsKey(className)) {
            String newName = "sample/package/" + RandomUtil.randomString(16);
            classNameMap.put(className, newName);
            System.out.println("\"" + className + "\" mapped to => \"" + newName + "\"");
        }
        return fetchNewName ? classNameMap.get(className) : className;
    }

    private static byte[] getManifest(InputStream inputStream, String mainClass) throws IOException {
        Manifest manifest = inputStream == null ? new Manifest() : new Manifest(inputStream);
        Attributes attributes = manifest.getMainAttributes();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (inputStream == null) attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attributes.put(Attributes.Name.MAIN_CLASS, mainClass);
        manifest.write(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
