package com.shinyhut.vernacular.client.exceptions;

public class AuthenticationRequiredException extends VncException {

    public AuthenticationRequiredException() {
        super("Server requires authentication but no username or password supplier was provided");
    }
}
