package com.shinyhut.vernacular.client.exceptions;

import static java.lang.String.format;

public class UnsupportedProtocolVersionException extends VncException {

    private final int serverMajor;
    private final int serverMinor;
    private final int requiredMajor;
    private final int requiredMinor;

    public UnsupportedProtocolVersionException(int serverMajor, int serverMinor, int minMajor, int minMinor) {
        super(format("The server supports protocol version %d.%d. We require version %d.%d",
                serverMajor, serverMinor, minMajor, minMinor));

        this.serverMajor = serverMajor;
        this.serverMinor = serverMinor;
        this.requiredMajor = minMajor;
        this.requiredMinor = minMinor;
    }

    public int getServerMajor() {
        return serverMajor;
    }

    public int getServerMinor() {
        return serverMinor;
    }

    public int getRequiredMajor() {
        return requiredMajor;
    }

    public int getRequiredMinor() {
        return requiredMinor;
    }
}
