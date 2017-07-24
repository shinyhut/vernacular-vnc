package com.shinyhut.vernacular.client.exceptions;

public class AuthenticationRequiredException extends VncException {

    public AuthenticationRequiredException() {
        super("Server requires authentication but no password supplier was provided");
    }
}
