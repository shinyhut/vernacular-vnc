package com.shinyhut.vernacular.protocol.messages;

import com.shinyhut.vernacular.client.exceptions.UnsupportedEncodingException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Rectangle {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final Encoding encoding;

    public Rectangle(int x, int y, int width, int height, Encoding encoding) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.encoding = encoding;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Encoding getEncoding() {
        return encoding;
    }

    public static Rectangle decode(InputStream in) throws UnsupportedEncodingException, IOException {
        DataInputStream dataInput = new DataInputStream(in);
        int x = dataInput.readUnsignedShort();
        int y = dataInput.readUnsignedShort();
        int width = dataInput.readUnsignedShort();
        int height = dataInput.readUnsignedShort();
        int encodingType = dataInput.readInt();
        Encoding encoding = Encoding.resolve(encodingType);
        return new Rectangle(x, y, width, height, encoding);
    }
}
