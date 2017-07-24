package com.shinyhut.vernacular.protocol.messages;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class PointerEvent implements Encodable {

    private final int x;
    private final int y;
    private final List<Boolean> buttons;

    public PointerEvent(int x, int y, List<Boolean> buttons) {
        this.x = x;
        this.y = y;
        this.buttons = buttons;
    }

    @Override
    public void encode(OutputStream out) throws IOException {
        DataOutput dataOutput = new DataOutputStream(out);
        dataOutput.write(0x05);
        dataOutput.write(buttonMask());
        dataOutput.writeShort(x);
        dataOutput.writeShort(y);
    }

    private byte buttonMask() {
        byte mask = 0x00;
        for (int i = 0; i < 8 && i < buttons.size(); i++) {
            if (buttons.get(i)) {
                mask |= (0x80 >> (7 - i));
            }
        }
        return mask;
    }
}
