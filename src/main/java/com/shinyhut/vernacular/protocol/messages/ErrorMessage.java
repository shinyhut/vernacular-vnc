package com.shinyhut.vernacular.protocol.messages;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class ErrorMessage {

    private final String message;

    private ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static ErrorMessage decode(InputStream input) throws IOException {
        DataInputStream dataInput = new DataInputStream(input);
        int errorMessageLength = dataInput.readInt();
        byte[] errorMessageBytes = new byte[errorMessageLength];
        dataInput.readFully(errorMessageBytes);
        return new ErrorMessage(new String(errorMessageBytes, Charset.forName("US-ASCII")));
    }

}
