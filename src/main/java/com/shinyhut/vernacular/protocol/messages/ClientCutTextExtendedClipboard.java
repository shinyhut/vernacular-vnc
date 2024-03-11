package com.shinyhut.vernacular.protocol.messages;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.Deflater;

import static com.shinyhut.vernacular.protocol.messages.MessageHeaderFlags.PROVIDE;
import static com.shinyhut.vernacular.protocol.messages.MessageHeaderFlags.TEXT;

public class ClientCutTextExtendedClipboard  implements Encodable {

    private final String text;

    public ClientCutTextExtendedClipboard(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public void encode(OutputStream out) throws IOException {
        int flags = PROVIDE.code | TEXT.code;

        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
        byte[] testLengthInBytes = ByteBuffer.allocate(4).putInt(textBytes.length + 1).array();
        byte[] input = new byte[4 + textBytes.length + 1];

        System.arraycopy(testLengthInBytes, 0, input, 0, testLengthInBytes.length);
        System.arraycopy(textBytes, 0, input, testLengthInBytes.length, textBytes.length);

        byte[] output = new byte[20 * 1024 * 1024];

        Deflater compresser = new Deflater();
        compresser.setInput(input);
        compresser.finish();
        int compressedDataLength = compresser.deflate(output);

        DataOutput dataOutput = new DataOutputStream(out);

        dataOutput.writeByte(0x06);
        dataOutput.write(new byte[3]);

        dataOutput.writeInt(-(compressedDataLength + 4));
        dataOutput.writeInt(flags);

        byte[] result = Arrays.copyOfRange(output, 0, compressedDataLength);

        dataOutput.write(result);
    }
}
