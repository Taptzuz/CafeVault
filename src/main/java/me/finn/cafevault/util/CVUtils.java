package me.finn.cafevault.util;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class CVUtils {

    public static @NotNull String formatClassToCVName(String className) {
        return removeExtension(className, ".class") + ".cv";
    }

    public static @NotNull String formatCVToClassName(String cvName) {
        return removeExtension(cvName, ".cv") + ".class";
    }

    public static @NotNull String formatCVTempToRes(String resName) {
        return removeExtension(resName, ".cvtemp") + ".cv";
    }

    public static @NotNull String formatEntryClassName(String className) {
        return removeExtension(className, ".class").replace("/", ".");
    }

    public static @NotNull String formatCVEntryName(@NotNull String className) {
        return className.replace(".", "/");
    }

    public static @NotNull String removeExtension(@NotNull String className, String extension) {
        if (className.endsWith(extension)) className = className.substring(0, className.length() - extension.length());
        return className;
    }

    public static byte[] in2ba(@NotNull InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[0xFFFF];
        for (int len = is.read(buffer); len != -1; len = is.read(buffer))
            os.write(buffer, 0, len);
        return os.toByteArray();
    }

    public static @NotNull String xor(@NotNull String data, int key) {
        return new String(xor(data.getBytes(StandardCharsets.UTF_8), key), StandardCharsets.UTF_8);
    }

    public static @NotNull byte[] xor(@NotNull byte[] data, int key) {
        int length = data.length;
        for (int i = 0; i < length; i++)
            data[i] ^= key;
        return data;
    }

    public static boolean classVerify(@NotNull byte[] entryBytes) {
        return String.format("%X%X%X%X", entryBytes[0], entryBytes[1], entryBytes[2], entryBytes[3]).toLowerCase().equals("cafebabe");
    }
}
