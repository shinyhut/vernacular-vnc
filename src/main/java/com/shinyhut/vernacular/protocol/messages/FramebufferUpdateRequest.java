package com.shinyhut.vernacular.protocol.messages;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FramebufferUpdateRequest implements Encodable {

    private final boolean incremental;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public FramebufferUpdateRequest(boolean incremental, int x, int y, int width, int height) {
        this.incremental = incremental;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void encode(OutputStream out) throws IOException {
        DataOutput dataOutput = new DataOutputStream(out);
        dataOutput.writeByte(0x03);
        dataOutput.writeBoolean(incremental);
        dataOutput.writeShort(x);
        dataOutput.writeShort(y);
        dataOutput.writeShort(width);
        dataOutput.writeShort(height);
    }
}
