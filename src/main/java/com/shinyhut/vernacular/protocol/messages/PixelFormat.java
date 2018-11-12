package com.shinyhut.vernacular.protocol.messages;

import java.io.*;

public class PixelFormat implements Encodable {

    private final int bitsPerPixel;
    private final int depth;
    private final boolean bigEndian;
    private final boolean trueColor;
    private final int redMax;
    private final int greenMax;
    private final int blueMax;
    private final int redShift;
    private final int greenShift;
    private final int blueShift;

    public PixelFormat(int bitsPerPixel, int depth, boolean bigEndian, boolean trueColor, int redMax,
                       int greenMax, int blueMax, int redShift, int greenShift, int blueShift) {
        this.bitsPerPixel = bitsPerPixel;
        this.depth = depth;
        this.bigEndian = bigEndian;
        this.trueColor = trueColor;
        this.redMax = redMax;
        this.greenMax = greenMax;
        this.blueMax = blueMax;
        this.redShift = redShift;
        this.greenShift = greenShift;
        this.blueShift = blueShift;
    }

    public int getBitsPerPixel() {
        return bitsPerPixel;
    }

    public int getBytesPerPixel() {
        return bitsPerPixel / 8;
    }

    public int getDepth() {
        return depth;
    }

    public boolean isBigEndian() {
        return bigEndian;
    }

    public boolean isTrueColor() {
        return trueColor;
    }

    public int getRedMax() {
        return redMax;
    }

    public int getGreenMax() {
        return greenMax;
    }

    public int getBlueMax() {
        return blueMax;
    }

    public int getRedShift() {
        return redShift;
    }

    public int getGreenShift() {
        return greenShift;
    }

    public int getBlueShift() {
        return blueShift;
    }

    @Override
    public void encode(OutputStream out) throws IOException {
        DataOutput dataOutput = new DataOutputStream(out);
        dataOutput.writeByte(bitsPerPixel);
        dataOutput.writeByte(depth);
        dataOutput.writeBoolean(bigEndian);
        dataOutput.writeBoolean(trueColor);
        dataOutput.writeShort(redMax);
        dataOutput.writeShort(greenMax);
        dataOutput.writeShort(blueMax);
        dataOutput.writeByte(redShift);
        dataOutput.writeByte(greenShift);
        dataOutput.writeByte(blueShift);
        dataOutput.write(new byte[3]);
    }

    public static PixelFormat decode(InputStream in) throws IOException {
        DataInputStream dataInput = new DataInputStream(in);
        int bpp = dataInput.readUnsignedByte();
        int depth = dataInput.readUnsignedByte();
        boolean bigEndian = dataInput.readBoolean();
        boolean trueColor = dataInput.readBoolean();
        int readMax = dataInput.readUnsignedShort();
        int greenMax = dataInput.readUnsignedShort();
        int blueMax = dataInput.readUnsignedShort();
        int redShift = dataInput.readUnsignedByte();
        int greenShift = dataInput.readUnsignedByte();
        int blueShift = dataInput.readUnsignedByte();
        dataInput.readFully(new byte[3]);
        return new PixelFormat(bpp, depth, bigEndian, trueColor, readMax, greenMax, blueMax, redShift, greenShift, blueShift);
    }
}
