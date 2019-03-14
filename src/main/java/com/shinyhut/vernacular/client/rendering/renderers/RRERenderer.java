package com.shinyhut.vernacular.client.rendering.renderers;

import com.shinyhut.vernacular.client.exceptions.UnexpectedVncException;
import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.protocol.messages.PixelFormat;
import com.shinyhut.vernacular.protocol.messages.Rectangle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RRERenderer implements Renderer {

    private final PixelDecoder pixelDecoder;
    private final PixelFormat pixelFormat;

    public RRERenderer(PixelDecoder pixelDecoder, PixelFormat pixelFormat) {
        this.pixelDecoder = pixelDecoder;
        this.pixelFormat = pixelFormat;
    }

    @Override
    public void render(InputStream in, BufferedImage destination, Rectangle rectangle) throws VncException {
        try {
            DataInput dataInput = new DataInputStream(in);
            int numberOfSubrectangles = dataInput.readInt();
            Pixel bgColor = pixelDecoder.decode(in, pixelFormat);

            Graphics2D graphic = (Graphics2D) destination.getGraphics();
            graphic.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue()));
            graphic.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());

            for (int i = 0; i < numberOfSubrectangles; i++) {
                Pixel color = pixelDecoder.decode(in, pixelFormat);
                int x = dataInput.readUnsignedShort();
                int y = dataInput.readUnsignedShort();
                int width = dataInput.readUnsignedShort();
                int height = dataInput.readUnsignedShort();
                graphic.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue()));
                graphic.fillRect(x + rectangle.getX(), y + rectangle.getY(), width, height);
            }
        } catch (IOException e) {
            throw new UnexpectedVncException(e);
        }
    }

}
