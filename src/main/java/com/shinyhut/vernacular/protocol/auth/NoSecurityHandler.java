package com.shinyhut.vernacular.protocol.auth;

import com.shinyhut.vernacular.client.VncSession;
import com.shinyhut.vernacular.protocol.messages.ProtocolVersion;
import com.shinyhut.vernacular.protocol.messages.SecurityResult;

import java.io.DataOutputStream;
import java.io.IOException;

public class NoSecurityHandler implements SecurityHandler {

    private static final byte NO_SECURITY_TYPE = 0x01;

    @Override
    public SecurityResult authenticate(VncSession session) throws IOException {
        new DataOutputStream(session.getOutputStream()).writeByte(NO_SECURITY_TYPE);
        ProtocolVersion protocolVersion = session.getProtocolVersion();
        if (protocolVersion.getMajor() == 3 && protocolVersion.getMinor() == 8) {
            return SecurityResult.decode(session.getInputStream(), session.getProtocolVersion());
        } else {
            return new SecurityResult(true);
        }
    }
}
