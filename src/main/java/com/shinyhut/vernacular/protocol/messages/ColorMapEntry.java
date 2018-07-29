package com.shinyhut.vernacular.protocol.messages;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ColorMapEntry {

    private final int red;
    private final int green;
    private final int blue;

    public ColorMapEntry(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public static ColorMapEntry decode(InputStream in) throws IOException {
        DataInputStream dataInput = new DataInputStream(in);
        int red = dataInput.readUnsignedShort();
        int green = dataInput.readUnsignedShort();
        int blue = dataInput.readUnsignedShort();
        return new ColorMapEntry(red, green, blue);
    }
}
