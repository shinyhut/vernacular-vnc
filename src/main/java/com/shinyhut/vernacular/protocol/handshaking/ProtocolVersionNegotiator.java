package com.shinyhut.vernacular.protocol.handshaking;

import com.shinyhut.vernacular.client.VncSession;
import com.shinyhut.vernacular.client.exceptions.UnsupportedProtocolVersionException;
import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.protocol.messages.ProtocolVersion;

import java.io.IOException;

public class ProtocolVersionNegotiator {

    private static final int MIN_MAJOR_VERSION = 3;
    private static final int MIN_MINOR_VERSION = 8;

    public void negotiate(VncSession session) throws IOException, VncException {
        ProtocolVersion serverVersion = ProtocolVersion.decode(session.getInputStream());
        if (isSupported(serverVersion)) {
            ProtocolVersion clientVersion = new ProtocolVersion(MIN_MAJOR_VERSION, MIN_MINOR_VERSION);
            clientVersion.encode(session.getOutputStream());
        } else {
            throw new UnsupportedProtocolVersionException(serverVersion.getMajor(), serverVersion.getMinor(), MIN_MAJOR_VERSION, MIN_MINOR_VERSION);
        }
    }

    private boolean isSupported(ProtocolVersion serverVersion) {
        return serverVersion.getMajor() >= MIN_MAJOR_VERSION && serverVersion.getMinor() >= MIN_MINOR_VERSION;
    }
}
