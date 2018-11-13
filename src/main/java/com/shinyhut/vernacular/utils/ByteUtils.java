package com.shinyhut.vernacular.utils;

public class ByteUtils {

    private ByteUtils() {
    }

    public static byte[] reverseBits(byte[] b) {
        byte[] result = new byte[b.length];
        for (int i = 0; i < b.length; i++) {
            result[i] = reverseBits(b[i]);
        }
        return result;
    }

    public static byte reverseBits(byte input) {
        byte result = 0x00;
        for (int i = 0; i < 8; i++) {
            result |= ((byte) ((input & (0x01 << i)) >>> i) << 7 - i);
        }
        return result;
    }

    public static boolean mask(int input, int mask) {
        return (input & mask) != 0;
    }
}
