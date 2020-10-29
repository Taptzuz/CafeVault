package me.finn.cafevault.classloader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class CafeClassLoader extends ClassLoader {

    private final HashMap<String, byte[]> classes, resources;

    public CafeClassLoader(HashMap<String, byte[]> classes, HashMap<String, byte[]> resources) {
        super(getSystemClassLoader());
        this.classes = classes;
        this.resources = resources;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (classes.containsKey(name)) {
            byte[] bytes = classes.get(name);

            this.classes.remove(name);

            return this.defineClass(name, bytes, 0, bytes.length, null);
        }
        return super.findClass(name);
    }

    @Override
    public URL getResource(String name) {
        try {
            byte[] bytes = this.resources.get(name);

            this.resources.remove(name);

            if (bytes != null)
                return new URL(null, "bytes:///" + name, new ByteStreamHandler(resources));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return super.getResource(name);
    }
}
