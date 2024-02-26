package com.shinyhut.vernacular.protocol.handshaking;

import com.shinyhut.vernacular.client.VncSession;
import com.shinyhut.vernacular.protocol.messages.ProtocolVersion;

import java.io.IOException;

public class ProtocolVersionNegotiator {

    private static final int MAJOR_VERSION = 3;
    private static final int MIN_MINOR_VERSION = 3;
    private static final int MAX_MINOR_VERSION = 8;

    public void negotiate(VncSession session) throws IOException {
        ProtocolVersion clientVersion = new ProtocolVersion(
                MAJOR_VERSION,
                MAX_MINOR_VERSION
        );

        session.setProtocolVersion(clientVersion);
        clientVersion.encode(session.getOutputStream());
    }
}
