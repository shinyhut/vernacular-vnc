package com.shinyhut.vernacular.client.rendering.renderers;

import com.shinyhut.vernacular.client.exceptions.UnexpectedVncException;
import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.protocol.messages.PixelFormat;
import com.shinyhut.vernacular.protocol.messages.Rectangle;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class CopyRectRenderer implements Renderer {

    @Override
    public void render(BufferedImage destination, Rectangle rectangle, PixelFormat pixelFormat) throws VncException {
        try {
            DataInput dataInput = new DataInputStream(new ByteArrayInputStream(rectangle.getPixelData()));
            int srcX = dataInput.readUnsignedShort();
            int srcY = dataInput.readUnsignedShort();
            BufferedImage src = new BufferedImage(rectangle.getWidth(), rectangle.getHeight(), TYPE_INT_RGB);
            destination.getSubimage(srcX, srcY, rectangle.getWidth(), rectangle.getHeight()).copyData(src.getRaster());
            destination.getGraphics().drawImage(src, rectangle.getX(), rectangle.getY(), null);
        } catch (IOException e) {
            throw new UnexpectedVncException(e);
        }
    }
}
