package com.shinyhut.vernacular.protocol.messages;

import com.shinyhut.vernacular.client.exceptions.UnsupportedEncodingException;
import com.shinyhut.vernacular.client.rendering.renderers.HextileRenderer;
import com.shinyhut.vernacular.utils.ByteUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static com.shinyhut.vernacular.client.rendering.renderers.HextileRenderer.*;
import static com.shinyhut.vernacular.utils.ByteUtils.mask;
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
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                int horizontalTiles = (int) Math.ceil((double) width / 16);
                int verticalTiles = (int) Math.ceil((double) height / 16);
                for (int ty = 0; ty < verticalTiles; ty++) {
                    for (int tx = 0; tx < horizontalTiles; tx++) {
                        int subencoding = dataInput.readUnsignedByte();
                        bytes.write(subencoding);
                        boolean raw = mask(subencoding, SUB_ENCODING_MASK_RAW);
                        if (raw) {
                            int tileWidth = tileSize(tx, horizontalTiles, width);
                            int tileHeight = tileSize(ty, verticalTiles, height);
                            byte[] subPixelData = new byte[tileWidth * tileHeight * bytesPerPixel];
                            dataInput.readFully(subPixelData);
                            bytes.write(subPixelData);
                        } else {
                            boolean backgroundSpecified = mask(subencoding, SUB_ENCODING_MASK_BACKGROUND_SPECIFIED);
                            boolean foregroundSpecified = mask(subencoding, SUB_ENCODING_MASK_FOREGROUND_SPECIFIED);
                            boolean anySubrects = mask(subencoding, SUB_ENCODING_MASK_ANY_SUBRECTS);
                            boolean subrectsColored = mask(subencoding, SUB_ENCODING_MASK_SUBRECTS_COLORED);
                            if (backgroundSpecified) {
                                byte[] background = new byte[bytesPerPixel];
                                dataInput.readFully(background);
                                bytes.write(background);
                            }
                            if (foregroundSpecified) {
                                byte[] foreground = new byte[bytesPerPixel];
                                dataInput.readFully(foreground);
                                bytes.write(foreground);
                            }
                            if (anySubrects) {
                                int subrectCount = dataInput.readUnsignedByte();
                                bytes.write(subrectCount);
                                int subrectDataLength = subrectCount * (subrectsColored ? bytesPerPixel + 2 : 2);
                                byte[] rectangleData = new byte[subrectDataLength];
                                dataInput.readFully(rectangleData);
                                bytes.write(rectangleData);
                            }
                        }
                    }
                }
                pixelData = bytes.toByteArray();
                break;
            case COPYRECT:
                pixelData = new byte[4];
                dataInput.readFully(pixelData);
                break;
            case RAW:
                pixelData = new byte[width * height * bytesPerPixel];
                dataInput.readFully(pixelData);
                break;
            default:
                throw new UnsupportedEncodingException(encoding.getCode());
        }
        return new Rectangle(x, y, width, height, encoding, pixelData);
    }
}
