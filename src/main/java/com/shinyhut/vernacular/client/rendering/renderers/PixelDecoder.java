package com.shinyhut.vernacular.client.rendering.renderers;

import com.shinyhut.vernacular.protocol.messages.ColorMapEntry;
import com.shinyhut.vernacular.protocol.messages.PixelFormat;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;

public class PixelDecoder {

    private static final ColorMapEntry BLACK = new ColorMapEntry(0, 0, 0);

    private final Map<BigInteger, ColorMapEntry> colorMap;

    public PixelDecoder(Map<BigInteger, ColorMapEntry> colorMap) {
        this.colorMap = colorMap;
    }

    public Pixel decode(InputStream in, PixelFormat pixelFormat) throws IOException {
        DataInput dataInput = new DataInputStream(in);
        byte[] bytes = new byte[pixelFormat.getBytesPerPixel()];
        dataInput.readFully(bytes);
        BigInteger value = new BigInteger(1, bytes);

        int red;
        int green;
        int blue;

        if (pixelFormat.isTrueColor()) {
            red = value.shiftRight(pixelFormat.getRedShift()).intValue() & pixelFormat.getRedMax();
            green = value.shiftRight(pixelFormat.getGreenShift()).intValue() & pixelFormat.getGreenMax();
            blue = value.shiftRight(pixelFormat.getBlueShift()).intValue() & pixelFormat.getBlueMax();

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
        return (int) (value * ((double) 255 / max));
    }

    private static int shrink(int colorMapValue) {
        return (int) Math.round(((double) colorMapValue) / 257);
    }
}
