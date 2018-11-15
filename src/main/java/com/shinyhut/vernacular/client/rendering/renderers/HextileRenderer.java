package com.shinyhut.vernacular.client.rendering.renderers;

import com.shinyhut.vernacular.client.exceptions.UnexpectedVncException;
import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.protocol.messages.PixelFormat;
import com.shinyhut.vernacular.protocol.messages.Rectangle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Optional;

import static com.shinyhut.vernacular.utils.ByteUtils.mask;
import static java.lang.Math.ceil;
import static java.util.Optional.empty;

public class HextileRenderer implements Renderer {

    public static final int SUB_ENCODING_MASK_RAW = 0x01;
    public static final int SUB_ENCODING_MASK_BACKGROUND_SPECIFIED = 0x02;
    public static final int SUB_ENCODING_MASK_FOREGROUND_SPECIFIED = 0x04;
    public static final int SUB_ENCODING_MASK_ANY_SUBRECTS = 0x08;
    public static final int SUB_ENCODING_MASK_SUBRECTS_COLORED = 0x10;

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
    public void render(BufferedImage destination, Rectangle rectangle) throws VncException {

        InputStream in = new ByteArrayInputStream(rectangle.getPixelData());
        DataInput dataInput = new DataInputStream(in);

        Graphics2D graphics = (Graphics2D) destination.getGraphics();

        int horizontalTiles = (int) ceil((double) rectangle.getWidth() / TILE_SIZE);
        int verticalTiles = (int) ceil((double) rectangle.getHeight() / TILE_SIZE);

        Pixel lastBackground = null;
        Pixel lastForeground = null;

        for (int ty = 0; ty < verticalTiles; ty++) {
            for (int tx = 0; tx < horizontalTiles; tx++) {
                try {
                    int sx = rectangle.getX() + (tx * TILE_SIZE);
                    int sy = rectangle.getY() + (ty * TILE_SIZE);
                    int width = tileSize(tx, horizontalTiles, rectangle.getWidth());
                    int height = tileSize(ty, verticalTiles, rectangle.getHeight());
                    int subencoding = dataInput.readUnsignedByte();
                    boolean raw = mask(subencoding, SUB_ENCODING_MASK_RAW);

                    if (raw) {
                        drawRawTile(destination, dataInput, sx, sy, width, height);
                    } else {
                        Pixel background = backgroundColor(in, subencoding).orElse(lastBackground);
                        Pixel foreground = foregroundColor(in, subencoding).orElse(lastForeground);
                        lastBackground = background;
                        lastForeground = foreground;
                        fillRect(graphics, sx, sy, width, height, background);

                        boolean anySubrects = mask(subencoding, SUB_ENCODING_MASK_ANY_SUBRECTS);
                        if (anySubrects) {
                            int subrectCount = dataInput.readUnsignedByte();
                            for (int s = 0; s < subrectCount; s++) {
                                renderSubrectangle(in, dataInput, graphics, sx, sy, subencoding, foreground);
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new UnexpectedVncException(e);
                }
            }
        }
    }

    public static int tileSize(int tileNo, int tileCount, int rectangleSize) {
        return tileNo == tileCount - 1 && rectangleSize % TILE_SIZE != 0 ? rectangleSize % TILE_SIZE : TILE_SIZE;
    }

    private void drawRawTile(BufferedImage img, DataInput in, int x, int y, int width, int height) throws IOException {
        byte[] pixelData = new byte[width * height * pixelFormat.getBytesPerPixel()];
        in.readFully(pixelData);
        rawRenderer.render(img, x, y, width, pixelData);
    }

    private void renderSubrectangle(InputStream in, DataInput dataInput, Graphics2D graphics, int x, int y,
                                    int subencoding, Pixel foreground) throws IOException {

        Pixel color = subrectangleColor(in, subencoding).orElse(foreground);
        int coords = dataInput.readUnsignedByte();
        int dimensions = dataInput.readUnsignedByte();
        int subrectX = coords >> 4;
        int subrectY = coords & 0x0f;
        int width = (dimensions >> 4) + 1;
        int height = (dimensions & 0x0f) + 1;
        int sx = x + subrectX;
        int sy = y + subrectY;
        fillRect(graphics, sx, sy, width, height, color);
    }

    private void fillRect(Graphics2D g, int x, int y, int width, int height, Pixel color) {
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue()));
        g.fillRect(x, y, width, height);
    }

    private Optional<Pixel> backgroundColor(InputStream in, int subencoding) throws IOException {
        return optionalPixel(in, subencoding, SUB_ENCODING_MASK_BACKGROUND_SPECIFIED);
    }

    private Optional<Pixel> foregroundColor(InputStream in, int subencoding) throws IOException {
        return optionalPixel(in, subencoding, SUB_ENCODING_MASK_FOREGROUND_SPECIFIED);
    }

    private Optional<Pixel> subrectangleColor(InputStream in, int subencoding) throws IOException {
        return optionalPixel(in, subencoding, SUB_ENCODING_MASK_SUBRECTS_COLORED);
    }

    private Optional<Pixel> optionalPixel(InputStream in, int subencoding, int mask) throws IOException {
        Optional<Pixel> pixel;
        if (mask(subencoding, mask)) {
            pixel = Optional.of(pixelDecoder.decode(in, pixelFormat));
        } else {
            pixel = empty();
        }
        return pixel;
    }

}
