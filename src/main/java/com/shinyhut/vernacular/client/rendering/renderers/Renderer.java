package com.shinyhut.vernacular.client.rendering.renderers;

import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.protocol.messages.Rectangle;

import java.awt.image.BufferedImage;
import java.io.InputStream;

public interface Renderer {
    void render(InputStream in, BufferedImage destination, Rectangle rectangle) throws VncException;
}
