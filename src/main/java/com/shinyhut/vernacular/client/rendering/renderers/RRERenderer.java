package com.shinyhut.vernacular.client.rendering.renderers;

import com.shinyhut.vernacular.client.exceptions.UnexpectedVncException;
import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.protocol.messages.PixelFormat;
import com.shinyhut.vernacular.protocol.messages.Rectangle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;

public class RRERenderer implements Renderer {

    private final PixelDecoder pixelDecoder;

    public RRERenderer() {
        this.pixelDecoder = new PixelDecoder();
    }

    @Override
    public void render(BufferedImage destination, Rectangle rectangle, PixelFormat pixelFormat) throws VncException {
        byte[] pixelData = rectangle.getPixelData();
        int bytesPerPixel = pixelFormat.getBitsPerPixel() / 8;

        DataInput dataInput = new DataInputStream(new ByteArrayInputStream(pixelData));

        try {
            int numberOfSubrectangles = dataInput.readInt();
            byte[] bgColourBytes = new byte[bytesPerPixel];
            dataInput.readFully(bgColourBytes);
            Pixel bgColour = pixelDecoder.decode(bgColourBytes, pixelFormat);

            Graphics2D graphic = (Graphics2D) destination.getGraphics();

            graphic.setColor(new Color(bgColour.getRed(), bgColour.getGreen(), bgColour.getBlue()));
            graphic.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());

            for (int i = 0; i < numberOfSubrectangles; i++) {
                byte[] bytes = new byte[bytesPerPixel];
                dataInput.readFully(bytes);
                int x = dataInput.readUnsignedShort();
                int y = dataInput.readUnsignedShort();
                int width = dataInput.readUnsignedShort();
                int height = dataInput.readUnsignedShort();
                Pixel colour = pixelDecoder.decode(bytes, pixelFormat);
                graphic.setColor(new Color(colour.getRed(), colour.getGreen(), colour.getBlue()));
                graphic.fillRect(x + rectangle.getX(), y + rectangle.getY(), width, height);
            }
        } catch (IOException e) {
            throw new UnexpectedVncException(e);
        }
    }

}
