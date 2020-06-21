package com.shinyhut.vernacular.protocol.handshaking;

import com.shinyhut.vernacular.client.VncSession;
import com.shinyhut.vernacular.client.exceptions.UnsupportedProtocolVersionException;
import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.protocol.messages.ProtocolVersion;

import java.io.IOException;

import static java.lang.Math.min;

public class ProtocolVersionNegotiator {

    private static final int MAJOR_VERSION = 3;
    private static final int MIN_MINOR_VERSION = 3;
    private static final int MAX_MINOR_VERSION = 8;

    public void negotiate(VncSession session) throws IOException, VncException {
        ProtocolVersion serverVersion = ProtocolVersion.decode(session.getInputStream());

        if (!serverVersion.atLeast(MAJOR_VERSION, MIN_MINOR_VERSION)) {
            throw new UnsupportedProtocolVersionException(
                    serverVersion.getMajor(),
                    serverVersion.getMinor(),
                    MAJOR_VERSION,
                    MIN_MINOR_VERSION
            );
        }

        ProtocolVersion clientVersion = new ProtocolVersion(
                MAJOR_VERSION,
                min(serverVersion.getMinor(), MAX_MINOR_VERSION)
        );

        session.setProtocolVersion(clientVersion);
        clientVersion.encode(session.getOutputStream());
    }
}
