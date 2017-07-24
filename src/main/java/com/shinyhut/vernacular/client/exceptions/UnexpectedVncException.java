package com.shinyhut.vernacular.client.exceptions;

public class UnexpectedVncException extends VncException {

    public UnexpectedVncException(Throwable cause) {
        super("An unexpected exception occured: " + cause.getClass().getSimpleName(), cause);
    }

}
