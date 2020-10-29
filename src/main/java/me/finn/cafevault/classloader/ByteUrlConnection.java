package me.finn.cafevault.classloader;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class ByteUrlConnection extends URLConnection {

    private final HashMap<String, byte[]> resources;

    public ByteUrlConnection(URL url, HashMap<String, byte[]> resources) {
        super(url);
        this.resources = resources;
    }

    @Override
    public void connect() {
    }

    @Override
    public final @NotNull InputStream getInputStream() {
        //TODO: Add decryption for resources
        return new ByteArrayInputStream(this.resources.get(this.getURL().getPath().substring(1)));
    }
}
