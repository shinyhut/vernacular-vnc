package com.shinyhut.vernacular.protocol.messages;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class KeyEvent implements Encodable {

    private final int keysym;
    private final boolean pressed;

    public KeyEvent(int keysym, boolean pressed) {
        this.keysym = keysym;
        this.pressed = pressed;
    }

    @Override
    public void encode(OutputStream out) throws IOException {
        DataOutput dataOutput = new DataOutputStream(out);
        dataOutput.writeByte(0x04);
        dataOutput.writeBoolean(pressed);
        dataOutput.write(new byte[]{0x00, 0x00});
        dataOutput.writeInt(keysym);
    }
}
