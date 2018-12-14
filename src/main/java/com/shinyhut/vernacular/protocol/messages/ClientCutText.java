package com.shinyhut.vernacular.protocol.messages;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class ClientCutText implements Encodable {

    private final String text;

    public ClientCutText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public void encode(OutputStream out) throws IOException {
        DataOutput dataOutput = new DataOutputStream(out);
        dataOutput.writeByte(0x06);
        dataOutput.write(new byte[3]);
        dataOutput.writeInt(text.length());
        dataOutput.write(text.getBytes(Charset.forName("ISO-8859-1")));
    }
}
