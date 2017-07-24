package com.shinyhut.vernacular.client.rendering.renderers;

import com.shinyhut.vernacular.protocol.messages.PixelFormat;
import com.shinyhut.vernacular.protocol.messages.Rectangle;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.System.arraycopy;

public class RawRenderer implements Renderer {

    private final PixelDecoder pixelDecoder;

    public RawRenderer() {
        this.pixelDecoder = new PixelDecoder();
    }

    @Override
    public void render(BufferedImage destination, Rectangle rectangle, PixelFormat pixelFormat) {
        byte[] pixelData = rectangle.getPixelData();
        int bytesPerPixel = pixelFormat.getBitsPerPixel() / 8;

        int x = rectangle.getX();
        int y = rectangle.getY();

        for (int i = 0; i <= pixelData.length - bytesPerPixel; i += bytesPerPixel) {
            byte[] bytes = new byte[bytesPerPixel];
            arraycopy(pixelData, i, bytes, 0, bytes.length);
            Pixel pixel = pixelDecoder.decode(bytes, pixelFormat);
            destination.setRGB(x, y, new Color(pixel.getRed(), pixel.getGreen(), pixel.getBlue()).getRGB());
            x++;
            if (x == rectangle.getX() + rectangle.getWidth()) {
                x = rectangle.getX();
                y++;
            }
        }
    }

}
