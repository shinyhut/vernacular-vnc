package com.shinyhut.vernacular.client.rendering.renderers;

import com.shinyhut.vernacular.protocol.messages.ColorMapEntry;
import com.shinyhut.vernacular.protocol.messages.PixelFormat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public class PixelDecoder {

    private static final ColorMapEntry BLACK = new ColorMapEntry(0, 0, 0);

    private final Map<Long, ColorMapEntry> colorMap;

    public PixelDecoder(Map<Long, ColorMapEntry> colorMap) {
        this.colorMap = colorMap;
    }

    public Pixel decode(InputStream in, PixelFormat pixelFormat) throws IOException {
        int bytesToRead = pixelFormat.getBytesPerPixel();
        long value = 0L;

        for (int i = 0; i < bytesToRead; i++) {
            value <<= 8;
            value |= in.read();
        }

        int red;
        int green;
        int blue;

        if (pixelFormat.isTrueColor()) {
            red = (int) (value >> pixelFormat.getRedShift()) & pixelFormat.getRedMax();
            green = (int) (value >> pixelFormat.getGreenShift()) & pixelFormat.getGreenMax();
            blue = (int) (value >> pixelFormat.getBlueShift()) & pixelFormat.getBlueMax();

            red = stretch(red, pixelFormat.getRedMax());
            green = stretch(green, pixelFormat.getGreenMax());
            blue = stretch(blue, pixelFormat.getBlueMax());
        } else {
            ColorMapEntry color = Optional.ofNullable(colorMap.get(value)).orElse(BLACK);
            red = shrink(color.getRed());
            green = shrink(color.getGreen());
            blue = shrink(color.getBlue());
        }

        return new Pixel(red, green, blue);
    }

    private static int stretch(int value, int max) {
        return max == 255 ? value : (int) (value * ((double) 255 / max));
    }

    private static int shrink(int colorMapValue) {
        return (int) Math.round(((double) colorMapValue) / 257);
    }
}
