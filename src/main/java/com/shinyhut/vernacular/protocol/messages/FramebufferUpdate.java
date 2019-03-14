package com.shinyhut.vernacular.protocol.messages;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FramebufferUpdate {

    private final int numberOfRectangles;

    public FramebufferUpdate(int numberOfRectangles) {
        this.numberOfRectangles = numberOfRectangles;
    }

    public int getNumberOfRectangles() {
        return numberOfRectangles;
    }

    public static FramebufferUpdate decode(InputStream in) throws IOException {
        DataInputStream dataInput = new DataInputStream(in);
        dataInput.readFully(new byte[2]);
        int numberOfRectangles = dataInput.readUnsignedShort();
        return new FramebufferUpdate(numberOfRectangles);
    }
}
