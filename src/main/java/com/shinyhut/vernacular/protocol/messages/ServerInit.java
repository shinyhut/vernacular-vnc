package com.shinyhut.vernacular.protocol.messages;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class ServerInit {

    private final int framebufferWidth;
    private final int framebufferHeight;
    private final PixelFormat pixelFormat;
    private final String name;

    private ServerInit(int framebufferWidth, int framebufferHeight, PixelFormat pixelFormat, String name) {
        this.framebufferWidth = framebufferWidth;
        this.framebufferHeight = framebufferHeight;
        this.pixelFormat = pixelFormat;
        this.name = name;
    }

    public int getFramebufferWidth() {
        return framebufferWidth;
    }

    public int getFramebufferHeight() {
        return framebufferHeight;
    }

    public PixelFormat getPixelFormat() {
        return pixelFormat;
    }

    public String getName() {
        return name;
    }

    public static ServerInit decode(InputStream in) throws IOException {
        DataInputStream dataInput = new DataInputStream(in);
        int framebufferWidth = dataInput.readUnsignedShort();
        int framebufferHeight = dataInput.readUnsignedShort();
        PixelFormat pixelFormat = PixelFormat.decode(in);
        int nameLength = dataInput.readInt();
        byte[] nameBytes = new byte[nameLength];
        dataInput.readFully(nameBytes);
        String name = new String(nameBytes, Charset.forName("US-ASCII"));
        return new ServerInit(framebufferWidth, framebufferHeight, pixelFormat, name);
    }
}
