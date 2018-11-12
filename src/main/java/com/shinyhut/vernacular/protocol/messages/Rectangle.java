package com.shinyhut.vernacular.protocol.messages;

import com.shinyhut.vernacular.client.exceptions.UnsupportedEncodingException;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static com.shinyhut.vernacular.client.rendering.renderers.HextileRenderer.*;
import static java.lang.System.arraycopy;

public class Rectangle {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final Encoding encoding;
    private final byte[] pixelData;

    public Rectangle(int x, int y, int width, int height, Encoding encoding, byte[] pixelData) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.encoding = encoding;
        this.pixelData = pixelData;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Encoding getEncoding() {
        return encoding;
    }

    public byte[] getPixelData() {
        return pixelData;
    }

    public static Rectangle decode(InputStream in, int bitsPerPixel) throws UnsupportedEncodingException, IOException {
        DataInputStream dataInput = new DataInputStream(in);
        int x = dataInput.readUnsignedShort();
        int y = dataInput.readUnsignedShort();
        int width = dataInput.readUnsignedShort();
        int height = dataInput.readUnsignedShort();
        int encodingType = dataInput.readInt();
        Encoding encoding = Encoding.resolve(encodingType);
        int bytesPerPixel = bitsPerPixel / 8;
        byte[] pixelData;
        switch (encoding) {
            case DESKTOP_SIZE:
                pixelData = new byte[0];
                break;
            case RRE:
                int subRecCount = dataInput.readInt();
                byte[] remaining = new byte[((bytesPerPixel + 8) * subRecCount) + bytesPerPixel];
                dataInput.readFully(remaining);
                pixelData = new byte[remaining.length + 4];
                byte[] subRecCountBytes = ByteBuffer.allocate(4).putInt(subRecCount).array();
                arraycopy(subRecCountBytes, 0, pixelData, 0, subRecCountBytes.length);
                arraycopy(remaining, 0, pixelData, 4, remaining.length);
                break;
            case HEXTILE:
                ByteArrayOutputStream o = new ByteArrayOutputStream();
                int horizontalTiles = (int) Math.ceil((double)width / 16);
                int verticalTiles = (int) Math.ceil((double)height / 16);
                for (int tileY = 0; tileY < verticalTiles; tileY++) {
                    for (int tileX = 0; tileX < horizontalTiles; tileX++) {
                        int subEncodingMask = dataInput.readUnsignedByte();
                        o.write(subEncodingMask);

                        boolean raw = (subEncodingMask & SUB_ENCODING_MASK_RAW) != 0;
                        boolean backgroundSpecified = (subEncodingMask & SUB_ENCODING_MASK_BACKGROUND_SPECIFIED) != 0;
                        boolean foregroundSpecified  = (subEncodingMask & SUB_ENCODING_MASK_FOREGROUND_SPECIFIED) != 0;
                        boolean anySubRects = (subEncodingMask & SUB_ENCODING_MASK_ANY_SUBRECTS) != 0;
                        boolean subrectsColored = (subEncodingMask & SUB_ENCODING_MASK_SUBRECTS_COLORED) != 0;

                        if (raw) {
                            int tileWidth;
                            int tileHeight;

                            if (tileX == horizontalTiles - 1) {
                                tileWidth = width % 16 == 0 ? 16 : width % 16;
                            } else {
                                tileWidth = 16;
                            }

                            if (tileY == verticalTiles - 1) {
                                tileHeight = height % 16 == 0 ? 16 : height % 16;
                            } else {
                                tileHeight = 16;
                            }

                            byte[] subPixelData = new byte[tileWidth * tileHeight * bytesPerPixel];
                            dataInput.readFully(subPixelData);
                            o.write(subPixelData);
                        } else {
                            if (backgroundSpecified) {
                                byte[] background = new byte[bytesPerPixel];
                                dataInput.readFully(background);
                                o.write(background);
                            }

                            if (foregroundSpecified) {
                                byte[] foreground = new byte[bytesPerPixel];
                                dataInput.readFully(foreground);
                                o.write(foreground);
                            }

                            if (anySubRects) {
                                int numberOfsubRectangles = dataInput.readUnsignedByte();
                                o.write(numberOfsubRectangles);
                                byte[] rectangleData = new byte[numberOfsubRectangles * (subrectsColored ? bytesPerPixel + 2 : 2)];
                                dataInput.readFully(rectangleData);
                                o.write(rectangleData);
                            }
                        }
                    }
                }
                pixelData = o.toByteArray();
                break;
            case COPYRECT:
                pixelData = new byte[4];
                dataInput.readFully(pixelData);
                break;
            case RAW:
            default:
                pixelData = new byte[width * height * bytesPerPixel];
                dataInput.readFully(pixelData);
                break;
        }
        return new Rectangle(x, y, width, height, encoding, pixelData);
    }
}
