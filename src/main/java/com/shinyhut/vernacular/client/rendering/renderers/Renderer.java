package com.shinyhut.vernacular.client.rendering.renderers;

import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.protocol.messages.ColorMapEntry;
import com.shinyhut.vernacular.protocol.messages.PixelFormat;
import com.shinyhut.vernacular.protocol.messages.Rectangle;

import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.util.Map;

public interface Renderer {
    void render(BufferedImage destination, Rectangle rectangle, PixelFormat pixelFormat,
                Map<BigInteger, ColorMapEntry> colorMap) throws VncException;
}
