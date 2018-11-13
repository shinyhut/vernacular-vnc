package com.shinyhut.vernacular.client.rendering.renderers;

import com.shinyhut.vernacular.client.exceptions.UnexpectedVncException;
import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.protocol.messages.ColorMapEntry;
import com.shinyhut.vernacular.protocol.messages.PixelFormat;
import com.shinyhut.vernacular.protocol.messages.Rectangle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

public class RRERenderer implements Renderer {

    private final PixelDecoder pixelDecoder;

    public RRERenderer() {
        this.pixelDecoder = new PixelDecoder();
    }

    @Override
    public void render(BufferedImage destination, Rectangle rectangle, PixelFormat pixelFormat,
                       Map<BigInteger, ColorMapEntry> colorMap) throws VncException {

        byte[] pixelData = rectangle.getPixelData();

        DataInput dataInput = new DataInputStream(new ByteArrayInputStream(pixelData));

        try {
            int numberOfSubrectangles = dataInput.readInt();
            byte[] bgColorBytes = new byte[pixelFormat.getBytesPerPixel()];
            dataInput.readFully(bgColorBytes);
            Pixel bgColor = pixelDecoder.decode(bgColorBytes, pixelFormat, colorMap);

            Graphics2D graphic = (Graphics2D) destination.getGraphics();

            graphic.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue()));
            graphic.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());

            for (int i = 0; i < numberOfSubrectangles; i++) {
                byte[] bytes = new byte[pixelFormat.getBytesPerPixel()];
                dataInput.readFully(bytes);
                int x = dataInput.readUnsignedShort();
                int y = dataInput.readUnsignedShort();
                int width = dataInput.readUnsignedShort();
                int height = dataInput.readUnsignedShort();
                Pixel color = pixelDecoder.decode(bytes, pixelFormat, colorMap);
                graphic.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue()));
                graphic.fillRect(x + rectangle.getX(), y + rectangle.getY(), width, height);
            }
        } catch (IOException e) {
            throw new UnexpectedVncException(e);
        }
    }

}
