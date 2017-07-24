package com.shinyhut.vernacular.protocol.messages;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class ServerInit {

    private final int frameBufferWidth;
    private final int frameBufferHeight;
    private final PixelFormat pixelFormat;
    private final String name;

    private ServerInit(int frameBufferWidth, int frameBufferHeight, PixelFormat pixelFormat, String name) {
        this.frameBufferWidth = frameBufferWidth;
        this.frameBufferHeight = frameBufferHeight;
        this.pixelFormat = pixelFormat;
        this.name = name;
    }

    public int getFrameBufferWidth() {
        return frameBufferWidth;
    }

    public int getFrameBufferHeight() {
        return frameBufferHeight;
    }

    public PixelFormat getPixelFormat() {
        return pixelFormat;
    }

    public String getName() {
        return name;
    }

    public static ServerInit decode(InputStream in) throws IOException {
        DataInputStream dataInput = new DataInputStream(in);
        int frameBufferWidth = dataInput.readUnsignedShort();
        int frameBufferHeight = dataInput.readUnsignedShort();
        PixelFormat pixelFormat = PixelFormat.decode(in);
        int nameLength = dataInput.readInt();
        byte[] nameBytes = new byte[nameLength];
        dataInput.readFully(nameBytes);
        String name = new String(nameBytes, Charset.forName("US-ASCII"));
        return new ServerInit(frameBufferWidth, frameBufferHeight, pixelFormat, name);
    }
}
