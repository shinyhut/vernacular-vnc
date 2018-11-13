package com.shinyhut.vernacular.client.rendering.renderers;

import com.shinyhut.vernacular.protocol.messages.PixelFormat;
import com.shinyhut.vernacular.protocol.messages.Rectangle;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.System.arraycopy;

public class RawRenderer implements Renderer {

    private final PixelDecoder pixelDecoder;
    private final PixelFormat pixelFormat;

    public RawRenderer(PixelDecoder pixelDecoder, PixelFormat pixelFormat) {
        this.pixelDecoder = pixelDecoder;
        this.pixelFormat = pixelFormat;
    }

    @Override
    public void render(BufferedImage destination, Rectangle rectangle) {
        render(destination, rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getPixelData());
    }

    public void render(BufferedImage destination, int x, int y, int width, byte[] pixelData) {

        int sx = x;
        int sy = y;

        for (int i = 0; i <= pixelData.length - pixelFormat.getBytesPerPixel(); i += pixelFormat.getBytesPerPixel()) {
            byte[] bytes = new byte[pixelFormat.getBytesPerPixel()];
            arraycopy(pixelData, i, bytes, 0, bytes.length);
            Pixel pixel = pixelDecoder.decode(bytes, pixelFormat);
            destination.setRGB(sx, sy, new Color(pixel.getRed(), pixel.getGreen(), pixel.getBlue()).getRGB());
            sx++;
            if (sx == x + width) {
                sx = x;
                sy++;
            }
        }
    }

}
