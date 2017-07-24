package com.shinyhut.vernacular.client.exceptions;

public class HandshakingFailedException extends VncException {

    private final String serverMessage;

    public HandshakingFailedException(String serverMessage) {
        super("VNC handshaking failed. The server returned the following error message: " + serverMessage);
        this.serverMessage = serverMessage;
    }

    public String getServerMessage() {
        return serverMessage;
    }

}
