package com.shinyhut.vernacular.protocol.auth;

import com.shinyhut.vernacular.client.VncSession;

import java.io.DataOutputStream;
import java.io.IOException;

public class NoSecurityHandler implements SecurityHandler {

    private static final byte NO_SECURITY_TYPE = 0x01;

    @Override
    public void authenticate(VncSession session) throws IOException {
        new DataOutputStream(session.getOutputStream()).writeByte(NO_SECURITY_TYPE);
    }
}
