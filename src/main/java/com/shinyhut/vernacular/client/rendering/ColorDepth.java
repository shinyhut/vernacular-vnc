package com.shinyhut.vernacular.client.rendering;

public enum ColorDepth {

    /** 8 bits per pixel **/
    BPP_8(8, 8, 7, 7, 3, 5, 2, 0),

    /** 16 bits per pixel **/
    BPP_16(16, 16, 31, 63, 31, 11, 5, 0),

    /** 24 bits per pixel **/
    BPP_24(32, 24, 255, 255, 255, 16, 8, 0);

    private final int bitsPerPixel;
    private final int depth;
    private final int redMax;
    private final int blueMax;
    private final int greenMax;
    private final int redShift;
    private final int blueShift;
    private final int greenShift;

    ColorDepth(int bitsPerPixel, int depth, int redMax, int blueMax, int greenMax, int redShift, int blueShift, int greenShift) {
        this.bitsPerPixel = bitsPerPixel;
        this.depth = depth;
        this.redMax = redMax;
        this.blueMax = blueMax;
        this.greenMax = greenMax;
        this.redShift = redShift;
        this.blueShift = blueShift;
        this.greenShift = greenShift;
    }

    public int getBitsPerPixel() {
        return bitsPerPixel;
    }

    public int getDepth() {
        return depth;
    }

    public int getRedMax() {
        return redMax;
    }

    public int getBlueMax() {
        return blueMax;
    }

    public int getGreenMax() {
        return greenMax;
    }

    public int getRedShift() {
        return redShift;
    }

    public int getBlueShift() {
        return blueShift;
    }

    public int getGreenShift() {
        return greenShift;
    }
}
