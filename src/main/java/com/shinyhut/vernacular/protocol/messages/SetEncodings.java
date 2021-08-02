package com.shinyhut.vernacular.protocol.messages;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static java.util.Arrays.asList;

public class SetEncodings implements Encodable {

    private final List<Encoding> encodings;

    public SetEncodings(Encoding... encodings) {
        this.encodings = asList(encodings);
    }

    public SetEncodings(List<Encoding> encodings) {
        this.encodings = encodings;
    }

    @Override
    public void encode(OutputStream out) throws IOException {
        DataOutputStream dataOutput = new DataOutputStream(out);
        dataOutput.writeByte(0x02);
        dataOutput.writeByte(0x00);
        dataOutput.writeShort(encodings.size());
        for (Encoding encoding : encodings) {
            dataOutput.writeInt(encoding.getCode());
        }

    }
}
