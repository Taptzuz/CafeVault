package me.finn.cafevault.util.jar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class JarWriter {

    private final JarOutputStream jarOutputStream;
    private final File jarOut;

    public JarWriter(File jarOut) throws IOException {
        this.jarOut = jarOut;
        jarOutputStream = new JarOutputStream(new FileOutputStream(this.jarOut));
    }

    public void write(String entryName, byte[] entryBytes) throws IOException {
        jarOutputStream.putNextEntry(new JarEntry(entryName));
        jarOutputStream.write(entryBytes);
        jarOutputStream.closeEntry();
    }

    public void finish() throws IOException {
        jarOutputStream.finish();
        jarOutputStream.flush();
        jarOutputStream.close();
    }

    public File getJarOut() {
        return jarOut;
    }
}
