package com.shinyhut.vernacular.protocol.auth;

import com.shinyhut.vernacular.client.VncSession;
import com.shinyhut.vernacular.protocol.messages.ProtocolVersion;
import com.shinyhut.vernacular.protocol.messages.SecurityResult;

import java.io.DataOutputStream;
import java.io.IOException;

import static com.shinyhut.vernacular.protocol.messages.SecurityType.NONE;

public class NoSecurityHandler implements SecurityHandler {

    @Override
    public SecurityResult authenticate(VncSession session) throws IOException {
        ProtocolVersion protocolVersion = session.getProtocolVersion();
        if (!protocolVersion.equals(3, 3)) {
            new DataOutputStream(session.getOutputStream()).writeByte(NONE.getCode());
        }
        if (protocolVersion.equals(3, 8)) {
            return SecurityResult.decode(session.getInputStream(), session.getProtocolVersion());
        } else {
            return new SecurityResult(true);
        }
    }
}
