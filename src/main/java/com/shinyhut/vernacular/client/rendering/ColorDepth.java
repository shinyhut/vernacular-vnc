package com.shinyhut.vernacular.client.rendering;

public enum ColorDepth {

    /** 8 bits per pixel indexed color **/
    BPP_8_INDEXED(8, 8, false, 0, 0, 0, 0, 0, 0),

    /** 8 bits per pixel true color **/
    BPP_8_TRUE(8, 8, true, 7, 3, 7, 0, 6, 3),

    /** 16 bits per pixel true color **/
    BPP_16_TRUE(16, 16, true, 31, 31, 63, 11, 0, 5),

    /** 24 bits per pixel true color **/
    BPP_24_TRUE(32, 24, true, 255, 255, 255, 8, 24, 16);

    private final int bitsPerPixel;
    private final int depth;
    private final boolean trueColor;
    private final int redMax;
    private final int blueMax;
    private final int greenMax;
    private final int redShift;
    private final int blueShift;
    private final int greenShift;

    ColorDepth(int bitsPerPixel, int depth, boolean trueColor, int redMax, int blueMax, int greenMax, int redShift, int blueShift, int greenShift) {
        this.bitsPerPixel = bitsPerPixel;
        this.depth = depth;
        this.trueColor = trueColor;
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

    public boolean isTrueColor() {
        return trueColor;
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
