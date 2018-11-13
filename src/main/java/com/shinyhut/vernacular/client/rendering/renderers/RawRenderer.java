package com.shinyhut.vernacular.client.rendering.renderers;

import com.shinyhut.vernacular.protocol.messages.ColorMapEntry;
import com.shinyhut.vernacular.protocol.messages.PixelFormat;
import com.shinyhut.vernacular.protocol.messages.Rectangle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.util.Map;

import static java.lang.System.arraycopy;

public class RawRenderer implements Renderer {

    private final PixelDecoder pixelDecoder;

    public RawRenderer() {
        this.pixelDecoder = new PixelDecoder();
    }

    @Override
    public void render(BufferedImage destination, Rectangle rectangle, PixelFormat pixelFormat,
                       Map<BigInteger, ColorMapEntry> colorMap) {

        render(destination, rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getPixelData(),
                pixelFormat, colorMap);
    }

    public void render(BufferedImage destination, int topLeftX, int topLeftY, int width, byte[] pixelData,
                       PixelFormat pixelFormat, Map<BigInteger, ColorMapEntry> colorMap) {

        int screenX = topLeftX;
        int screenY = topLeftY;

        for (int i = 0; i <= pixelData.length - pixelFormat.getBytesPerPixel(); i += pixelFormat.getBytesPerPixel()) {
            byte[] bytes = new byte[pixelFormat.getBytesPerPixel()];
            arraycopy(pixelData, i, bytes, 0, bytes.length);
            Pixel pixel = pixelDecoder.decode(bytes, pixelFormat, colorMap);
            destination.setRGB(screenX, screenY, new Color(pixel.getRed(), pixel.getGreen(), pixel.getBlue()).getRGB());
            screenX++;
            if (screenX == topLeftX + width) {
                screenX = topLeftX;
                screenY++;
            }
        }
    }

}
