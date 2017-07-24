package com.shinyhut.vernacular.protocol.handshaking;

import com.shinyhut.vernacular.client.VncSession;
import com.shinyhut.vernacular.client.exceptions.AuthenticationFailedException;
import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.protocol.auth.SecurityHandler;
import com.shinyhut.vernacular.protocol.messages.SecurityResult;

import java.io.IOException;

public class Handshaker {

    private final ProtocolVersionNegotiator protocolVersionNegotiator;
    private final SecurityTypeNegotiator securityTypeNegotiator;

    public Handshaker() {
        protocolVersionNegotiator = new ProtocolVersionNegotiator();
        securityTypeNegotiator = new SecurityTypeNegotiator();
    }

    public void handshake(VncSession session) throws VncException, IOException {
        protocolVersionNegotiator.negotiate(session);

        SecurityHandler securityHandler = securityTypeNegotiator.negotiate(session);
        securityHandler.authenticate(session);
        SecurityResult securityResult = SecurityResult.decode(session.getInputStream());

        if (!securityResult.isSuccess()) {
            throw new AuthenticationFailedException(securityResult.getErrorMessage());
        }
    }
}
