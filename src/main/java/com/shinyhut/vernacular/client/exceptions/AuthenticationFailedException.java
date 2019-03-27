package com.shinyhut.vernacular.client.exceptions;

public class AuthenticationFailedException extends VncException {

    private final String serverMessage;

    public AuthenticationFailedException() {
        super("Authentication failed");
        serverMessage = null;
    }

    public AuthenticationFailedException(String serverMessage) {
        super("Authentication failed. The server returned the following extra information: " + serverMessage);
        this.serverMessage = serverMessage;
    }

    public String getServerMessage() {
        return serverMessage;
    }

}
