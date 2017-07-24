package com.shinyhut.vernacular.client.exceptions;

import static java.lang.String.format;

public class UnsupportedEncodingException extends VncException {

    private final int encodingType;

    public int getEncodingType() {
        return encodingType;
    }

    public UnsupportedEncodingException(int encodingType) {
        super(format("Unsupported encoding type: %d", encodingType));

        this.encodingType = encodingType;
    }
}
