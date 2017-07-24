package com.shinyhut.vernacular.protocol.messages;

import com.shinyhut.vernacular.client.exceptions.UnsupportedEncodingException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FramebufferUpdate {

    private final List<Rectangle> rectangles;

    public FramebufferUpdate(List<Rectangle> rectangles) {
        this.rectangles = rectangles;
    }

    public List<Rectangle> getRectangles() {
        return rectangles;
    }

    public static FramebufferUpdate decode(InputStream in, int bitsPerPixel) throws UnsupportedEncodingException, IOException {
        DataInputStream dataInput = new DataInputStream(in);
        dataInput.readFully(new byte[2]);
        int numberOfRectangles = dataInput.readUnsignedShort();
        List<Rectangle> rectangles = new ArrayList<>();
        for (int i = 0; i < numberOfRectangles; i++) {
            rectangles.add(Rectangle.decode(in, bitsPerPixel));
        }
        return new FramebufferUpdate(rectangles);
    }
}
