package com.shinyhut.vernacular.client.rendering.renderers;

import com.shinyhut.vernacular.client.exceptions.UnexpectedVncException;
import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.protocol.messages.PixelFormat;
import com.shinyhut.vernacular.protocol.messages.Rectangle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import static com.shinyhut.vernacular.utils.ByteUtils.mask;
import static java.lang.Math.ceil;

public class HextileRenderer implements Renderer {

    public static final int SUB_ENCODING_MASK_RAW = 0x01;
    public static final int SUB_ENCODING_MASK_BACKGROUND_SPECIFIED = 0x02;
    public static final int SUB_ENCODING_MASK_FOREGROUND_SPECIFIED = 0x04;
    public static final int SUB_ENCODING_MASK_ANY_SUBRECTS = 0x08;
    public static final int SUB_ENCODING_MASK_SUBRECTS_COLORED = 0x10;

    private static final int TILE_SIZE = 16;

    private final PixelDecoder pixelDecoder;
    private final RawRenderer rawRenderer;

    public HextileRenderer(RawRenderer rawRenderer, PixelDecoder pixelDecoder) {
        this.pixelDecoder = pixelDecoder;
        this.rawRenderer = rawRenderer;
    }

    @Override
    public void render(BufferedImage destination, Rectangle rectangle, PixelFormat pixelFormat) throws VncException {

        byte[] pixelData = rectangle.getPixelData();
        int bytesPerPixel = pixelFormat.getBytesPerPixel();

        InputStream in = new ByteArrayInputStream(pixelData);
        DataInput dataInput = new DataInputStream(in);

        Graphics2D graphics = (Graphics2D) destination.getGraphics();

        int horizontalTileCount = (int) ceil((double) rectangle.getWidth() / TILE_SIZE);
        int verticalTileCount = (int) ceil((double) rectangle.getHeight() / TILE_SIZE);

        Pixel previousBackground = null;
        Pixel previousForeground = null;

        for (int tileY = 0; tileY < verticalTileCount; tileY++) {
            for (int tileX = 0; tileX < horizontalTileCount; tileX++) {
                try {
                    int subEncoding = dataInput.readUnsignedByte();

                    boolean raw = mask(subEncoding, SUB_ENCODING_MASK_RAW);
                    boolean backgroundSpecified = mask(subEncoding, SUB_ENCODING_MASK_BACKGROUND_SPECIFIED);
                    boolean foregroundSpecified = mask(subEncoding, SUB_ENCODING_MASK_FOREGROUND_SPECIFIED);
                    boolean anySubrects = mask(subEncoding, SUB_ENCODING_MASK_ANY_SUBRECTS);
                    boolean subrectsColored = mask(subEncoding, SUB_ENCODING_MASK_SUBRECTS_COLORED);

                    int tileTopLeftX = rectangle.getX() + (tileX * TILE_SIZE);
                    int tileTopLeftY = rectangle.getY() + (tileY * TILE_SIZE);

                    int tileWidth;
                    int tileHeight;

                    if (tileX == horizontalTileCount - 1 && rectangle.getWidth() % TILE_SIZE != 0) {
                        tileWidth = rectangle.getWidth() % TILE_SIZE;
                    } else {
                        tileWidth = TILE_SIZE;
                    }

                    if (tileY == verticalTileCount - 1 && rectangle.getHeight() % TILE_SIZE != 0) {
                        tileHeight = rectangle.getHeight() % TILE_SIZE;
                    } else {
                        tileHeight = TILE_SIZE;
                    }

                    if (raw) {
                        byte[] subPixelData = new byte[tileWidth * tileHeight * bytesPerPixel];
                        dataInput.readFully(subPixelData);
                        rawRenderer.render(destination, tileTopLeftX, tileTopLeftY, tileWidth, subPixelData, pixelFormat);
                    } else {
                        Pixel background = optionalPixel(in, pixelFormat, backgroundSpecified, previousBackground);
                        Pixel foreground = optionalPixel(in, pixelFormat, foregroundSpecified, previousForeground);
                        previousBackground = background;
                        previousForeground = foreground;

                        graphics.setColor(new Color(background.getRed(), background.getGreen(), background.getBlue()));
                        graphics.fillRect(tileTopLeftX, tileTopLeftY, tileWidth, tileHeight);

                        if (anySubrects) {
                            int subrectCount = dataInput.readUnsignedByte();

                            for (int s = 0; s < subrectCount; s++) {
                                Pixel subrectColor = optionalPixel(in, pixelFormat, subrectsColored, foreground);

                                int coords = dataInput.readUnsignedByte();
                                int dimensions = dataInput.readUnsignedByte();

                                int subrectTopLeftX = coords >> 4;
                                int subrectTopLeftY = coords & 0x0f;

                                int width = (dimensions >> 4) + 1;
                                int height = (dimensions & 0x0f) + 1;

                                int sx = tileTopLeftX + subrectTopLeftX;
                                int sy = tileTopLeftY + subrectTopLeftY;

                                graphics.setColor(new Color(subrectColor.getRed(), subrectColor.getGreen(), subrectColor.getBlue()));
                                graphics.fillRect(sx, sy, width, height);
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new UnexpectedVncException(e);
                }
            }
        }
    }

    private Pixel optionalPixel(InputStream in, PixelFormat pixelFormat, boolean present, Pixel defaultValue) throws IOException {
        Pixel pixel;
        if (present) {
            pixel = pixelDecoder.decode(in, pixelFormat);
        } else {
            pixel = defaultValue;
        }
        return pixel;
    }

}
