package com.shinyhut.vernacular.protocol.messages;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

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
        byte[] textBytes = new byte[textLength];
        dataInput.readFully(textBytes);
        String text = new String(textBytes, Charset.forName("ISO-8859-1"));
        return new ServerCutText(text);
    }
}
