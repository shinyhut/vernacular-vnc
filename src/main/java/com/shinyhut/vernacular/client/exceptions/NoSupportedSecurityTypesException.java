package com.shinyhut.vernacular.client.exceptions;

public class NoSupportedSecurityTypesException extends VncException {

    public NoSupportedSecurityTypesException() {
        super("The server does not support any VNC security types supported by this client");
    }

}
