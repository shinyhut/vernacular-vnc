package com.shinyhut.vernacular.client.rendering.renderers;

import com.shinyhut.vernacular.protocol.messages.PixelFormat;
import com.shinyhut.vernacular.protocol.messages.Rectangle;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.System.arraycopy;

public class RawRenderer implements Renderer {

    private final PixelDecoder pixelDecoder;

    public RawRenderer(PixelDecoder pixelDecoder) {
        this.pixelDecoder = pixelDecoder;
    }

    @Override
    public void render(BufferedImage destination, Rectangle rectangle, PixelFormat pixelFormat) {
        render(destination, rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getPixelData(), pixelFormat);
    }

    public void render(BufferedImage destination, int x, int y, int width, byte[] pixelData, PixelFormat pixelFormat) {

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
