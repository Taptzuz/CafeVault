package me.finn.cafevault.classloader;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;

public class ByteStreamHandler extends URLStreamHandler {

    private final HashMap<String, byte[]> resources;

    public ByteStreamHandler(HashMap<String, byte[]> resources) {
        this.resources = resources;
    }

    @Override
    protected URLConnection openConnection(URL u) {
        return new ByteUrlConnection(u, this.resources);
    }
}
