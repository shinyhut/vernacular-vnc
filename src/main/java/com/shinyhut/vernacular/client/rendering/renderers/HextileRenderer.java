package com.shinyhut.vernacular.client.rendering.renderers;

import com.shinyhut.vernacular.client.exceptions.UnexpectedVncException;
import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.protocol.messages.ColorMapEntry;
import com.shinyhut.vernacular.protocol.messages.PixelFormat;
import com.shinyhut.vernacular.protocol.messages.Rectangle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.util.Map;

import static java.lang.Math.ceil;
import static java.lang.System.arraycopy;

public class HextileRenderer implements Renderer {

    public static final int SUB_ENCODING_MASK_RAW = 0x01;
    public static final int SUB_ENCODING_MASK_BACKGROUND_SPECIFIED = 0x02;
    public static final int SUB_ENCODING_MASK_FOREGROUND_SPECIFIED = 0x04;
    public static final int SUB_ENCODING_MASK_ANY_SUBRECTS = 0x08;
    public static final int SUB_ENCODING_MASK_SUBRECTS_COLORED = 0x10;

    private static final int TILE_SIZE = 16;

    private final PixelDecoder pixelDecoder;

    public HextileRenderer() {
        this.pixelDecoder = new PixelDecoder();
    }

    @Override
    public void render(BufferedImage destination, Rectangle rectangle, PixelFormat pixelFormat,
                       Map<BigInteger, ColorMapEntry> colorMap) throws VncException {

        byte[] pixelData = rectangle.getPixelData();
        int bytesPerPixel = pixelFormat.getBitsPerPixel() / 8;

        InputStream in = new ByteArrayInputStream(pixelData);
        DataInput dataInput = new DataInputStream(in);

        Graphics2D graphics = (Graphics2D) destination.getGraphics();

        int rectangleWidth = rectangle.getWidth();
        int rectangleHeight = rectangle.getHeight();

        int horizontalTileCount = (int) ceil((double) rectangleWidth / TILE_SIZE);
        int verticalTileCount = (int) ceil((double) rectangleHeight / TILE_SIZE);

        Pixel previousBackground = null;
        Pixel previousForeground = null;

        for (int tileY = 0; tileY < verticalTileCount; tileY++) {
            for (int tileX = 0; tileX < horizontalTileCount; tileX++) {
                try {
                    int subEncodingMask = dataInput.readUnsignedByte();

                    boolean raw = (subEncodingMask & SUB_ENCODING_MASK_RAW) != 0;
                    boolean backgroundSpecified = (subEncodingMask & SUB_ENCODING_MASK_BACKGROUND_SPECIFIED) != 0;
                    boolean foregroundSpecified = (subEncodingMask & SUB_ENCODING_MASK_FOREGROUND_SPECIFIED) != 0;
                    boolean anySubrects = (subEncodingMask & SUB_ENCODING_MASK_ANY_SUBRECTS) != 0;
                    boolean subrectsColored = (subEncodingMask & SUB_ENCODING_MASK_SUBRECTS_COLORED) != 0;

                    int tileTopLeftX = rectangle.getX() + (tileX * TILE_SIZE);
                    int tileTopLeftY = rectangle.getY() + (tileY * TILE_SIZE);

                    int tileWidth;
                    int tileHeight;

                    if (tileX == horizontalTileCount - 1 && rectangleWidth % TILE_SIZE != 0) {
                        tileWidth = rectangleWidth % TILE_SIZE;
                    } else {
                        tileWidth = TILE_SIZE;
                    }

                    if (tileY == verticalTileCount - 1 && rectangleHeight % TILE_SIZE != 0) {
                        tileHeight = rectangleHeight % TILE_SIZE;
                    } else {
                        tileHeight = TILE_SIZE;
                    }

                    if (raw) {
                        byte[] subPixelData = new byte[tileWidth * tileHeight * bytesPerPixel];
                        dataInput.readFully(subPixelData);

                        int screenX = tileTopLeftX;
                        int screenY = tileTopLeftY;

                        for (int i = 0; i <= subPixelData.length - bytesPerPixel; i += bytesPerPixel) {
                            byte[] bytes = new byte[bytesPerPixel];
                            arraycopy(subPixelData, i, bytes, 0, bytes.length);
                            Pixel pixel = pixelDecoder.decode(bytes, pixelFormat, colorMap);
                            destination.setRGB(screenX, screenY, new Color(pixel.getRed(), pixel.getGreen(), pixel.getBlue()).getRGB());
                            screenX++;
                            if (screenX == (rectangle.getX() + (tileX * TILE_SIZE + tileWidth))) {
                                screenX = tileTopLeftX;
                                screenY++;
                            }
                        }

                    } else {
                        Pixel background;
                        if (backgroundSpecified) {
                            background = pixelDecoder.decode(in, pixelFormat, colorMap);
                            previousBackground = background;
                        } else {
                            background = previousBackground;
                        }

                        Pixel foreground;
                        if (foregroundSpecified) {
                            foreground = pixelDecoder.decode(in, pixelFormat, colorMap);
                            previousForeground = foreground;
                        } else {
                            foreground = previousForeground;
                        }

                        graphics.setColor(new Color(background.getRed(), background.getGreen(), background.getBlue()));
                        graphics.fillRect(tileTopLeftX, tileTopLeftY, tileWidth, tileHeight);

                        if (anySubrects) {
                            int subrectCount = dataInput.readUnsignedByte();

                            for (int s = 0; s < subrectCount; s++) {
                                Pixel pixelColor;
                                if (subrectsColored) {
                                    pixelColor = pixelDecoder.decode(in, pixelFormat, colorMap);
                                } else {
                                    pixelColor = foreground;
                                }

                                int coords = dataInput.readUnsignedByte();
                                int dimensions = dataInput.readUnsignedByte();

                                int x = coords >> 4;
                                int y = coords & 0x0f;

                                int width = (dimensions >> 4) + 1;
                                int height = (dimensions & 0x0f) + 1;

                                graphics.setColor(new Color(pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue()));
                                graphics.fillRect(tileTopLeftX + x, tileTopLeftY + y, width, height);

                            }
                        }
                    }
                } catch (IOException e) {
                    throw new UnexpectedVncException(e);
                }
            }
        }
    }

}
