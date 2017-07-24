package com.shinyhut.vernacular.protocol.messages;

import java.io.IOException;
import java.io.OutputStream;

public class SetPixelFormat implements Encodable {

    private final PixelFormat pixelFormat;

    public SetPixelFormat(PixelFormat pixelFormat) {
        this.pixelFormat = pixelFormat;
    }

    @Override
    public void encode(OutputStream out) throws IOException {
        out.write(0x00);
        out.write(new byte[3]);
        pixelFormat.encode(out);
    }
}
