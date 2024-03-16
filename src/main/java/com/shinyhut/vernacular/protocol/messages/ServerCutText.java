package com.shinyhut.vernacular.protocol.messages;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class ServerCutText {

    private final String text;

    public ServerCutText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static ServerCutText decode(InputStream in) throws IOException {
        DataInputStream dataInput = new DataInputStream(in);
        dataInput.readFully(new byte[4]);

        int textLength = dataInput.readInt();

        if (textLength < 0) {
            return decodeExtendedMessageFormat(dataInput, -textLength);
        }

        return decodeOriginalFormat(dataInput, textLength);
    }

    private static ServerCutText decodeOriginalFormat(DataInputStream dataInput, Integer textLength) throws IOException {
        byte[] textBytes = new byte[textLength];
        dataInput.readFully(textBytes);
        String text = new String(textBytes, StandardCharsets.ISO_8859_1);
        return new ServerCutText(text);
    }

    private static ServerCutText decodeExtendedMessageFormat(DataInputStream dataInput, Integer textLength) throws IOException {
        Charset charset = StandardCharsets.UTF_8;

        int flags = dataInput.readInt();

        byte[] textBytes = new byte[textLength - 4];
        dataInput.readFully(textBytes);

        if ((flags & MessageHeaderFlags.CAPS.code) != 0) {
            return new ServerCutText("");
        }

        if ((flags & MessageHeaderFlags.PROVIDE.code) != 0) {
            Inflater inflater = new Inflater();

            inflater.setInput(textBytes, 0, textBytes.length);
            byte[] result = new byte[20 * 1024 * 1024];
            try {
                inflater.inflate(result);
            } catch (DataFormatException e) {
                throw new RuntimeException(e);
            } finally {
                inflater.end();
            }
            int length = new BigInteger(Arrays.copyOfRange(result, 0, 4)).intValue();
            String text = new String(result, 4, length - 1, charset);
            return new ServerCutText(text);
        }

        return new ServerCutText("");
    }

}
