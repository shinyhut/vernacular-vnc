package com.shinyhut.vernacular.client.rendering.renderers;

import com.shinyhut.vernacular.protocol.messages.PixelFormat;

import java.math.BigInteger;

public class PixelDecoder {

    public Pixel decode(byte[] bytes, PixelFormat pixelFormat) {
        BigInteger value = new BigInteger(1, bytes);

        int red = value.shiftRight(pixelFormat.getRedShift()).intValue() & pixelFormat.getRedMax();
        int green = value.shiftRight(pixelFormat.getGreenShift()).intValue() & pixelFormat.getGreenMax();
        int blue = value.shiftRight(pixelFormat.getBlueShift()).intValue() & pixelFormat.getBlueMax();

        red = stretch(red, pixelFormat.getRedMax());
        green = stretch(green, pixelFormat.getGreenMax());
        blue = stretch(blue, pixelFormat.getBlueMax());

        return new Pixel(red, green, blue);
    }

    private static int stretch(int value, int max) {
        return (int) (value * ((double) 255 / max));
    }
}
