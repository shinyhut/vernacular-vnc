package com.shinyhut.vernacular.client.rendering.renderers;

import com.shinyhut.vernacular.client.exceptions.UnexpectedVncException;
import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.protocol.messages.PixelFormat;
import com.shinyhut.vernacular.protocol.messages.Rectangle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.shinyhut.vernacular.utils.ByteUtils.mask;

public class HextileRenderer implements Renderer {

    private static final int SUB_ENCODING_MASK_RAW = 0x01;
    private static final int SUB_ENCODING_MASK_BACKGROUND_SPECIFIED = 0x02;
    private static final int SUB_ENCODING_MASK_FOREGROUND_SPECIFIED = 0x04;
    private static final int SUB_ENCODING_MASK_ANY_SUBRECTS = 0x08;
    private static final int SUB_ENCODING_MASK_SUBRECTS_COLORED = 0x10;

    private static final int TILE_SIZE = 16;

    private final RawRenderer rawRenderer;
    private final PixelDecoder pixelDecoder;
    private final PixelFormat pixelFormat;

    public HextileRenderer(RawRenderer rawRenderer, PixelDecoder pixelDecoder, PixelFormat pixelFormat) {
        this.pixelDecoder = pixelDecoder;
        this.rawRenderer = rawRenderer;
        this.pixelFormat = pixelFormat;
    }

    @Override
    public void render(InputStream in, BufferedImage destination, Rectangle rectangle) throws VncException {
        DataInput dataInput = new DataInputStream(in);
        Graphics2D g = (Graphics2D) destination.getGraphics();

        int horizontalTileCount = (rectangle.getWidth() + TILE_SIZE - 1) / TILE_SIZE;
        int verticalTileCount = (rectangle.getHeight() + TILE_SIZE - 1) / TILE_SIZE;

        Pixel lastBackground = null;
        Pixel lastForeground = null;

        try {
            for (int tileY = 0; tileY < verticalTileCount; tileY++) {
                for (int tileX = 0; tileX < horizontalTileCount; tileX++) {
                    int tileTopLeftX = rectangle.getX() + (tileX * TILE_SIZE);
                    int tileTopLeftY = rectangle.getY() + (tileY * TILE_SIZE);
                    int tileWidth = tileSize(tileX, horizontalTileCount, rectangle.getWidth());
                    int tileHeight = tileSize(tileY, verticalTileCount, rectangle.getHeight());
                    int subencoding = dataInput.readUnsignedByte();
                    boolean raw = mask(subencoding, SUB_ENCODING_MASK_RAW);

                    if (raw) {
                        rawRenderer.render(in, destination, tileTopLeftX, tileTopLeftY, tileWidth, tileHeight);
                    } else {
                        boolean hasBackground = mask(subencoding, SUB_ENCODING_MASK_BACKGROUND_SPECIFIED);
                        boolean hasForeground = mask(subencoding, SUB_ENCODING_MASK_FOREGROUND_SPECIFIED);
                        boolean hasSubrects = mask(subencoding, SUB_ENCODING_MASK_ANY_SUBRECTS);
                        boolean subrectsColored = mask(subencoding, SUB_ENCODING_MASK_SUBRECTS_COLORED);

                        Pixel background = hasBackground ? pixelDecoder.decode(in, pixelFormat) : lastBackground;
                        Pixel foreground = hasForeground ? pixelDecoder.decode(in, pixelFormat) : lastForeground;
                        lastBackground = background;
                        lastForeground = foreground;

                        g.setColor(new Color(background.getRed(), background.getGreen(), background.getBlue()));
                        g.fillRect(tileTopLeftX, tileTopLeftY, tileWidth, tileHeight);

                        if (hasSubrects) {
                            int subrectCount = dataInput.readUnsignedByte();
                            for (int s = 0; s < subrectCount; s++) {
                                Pixel subrectColor = subrectsColored ? pixelDecoder.decode(in, pixelFormat) : foreground;
                                int coords = dataInput.readUnsignedByte();
                                int dimensions = dataInput.readUnsignedByte();
                                int subrectX = coords >> 4;
                                int subrectY = coords & 0x0f;
                                int subrectWidth = (dimensions >> 4) + 1;
                                int subrectHeight = (dimensions & 0x0f) + 1;
                                int subrectTopLeftX = tileTopLeftX + subrectX;
                                int subrectTopLeftY = tileTopLeftY + subrectY;
                                g.setColor(new Color(subrectColor.getRed(), subrectColor.getGreen(), subrectColor.getBlue()));
                                g.fillRect(subrectTopLeftX, subrectTopLeftY, subrectWidth, subrectHeight);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new UnexpectedVncException(e);
        }
    }

    private static int tileSize(int tileNo, int numberOfTiles, int rectangleSize) {
        int overlap = rectangleSize % TILE_SIZE;
        if (tileNo == numberOfTiles -1 && overlap != 0) {
            return overlap;
        }
        return TILE_SIZE;
    }
}
