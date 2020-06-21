package com.shinyhut.vernacular.protocol.handshaking;

import com.shinyhut.vernacular.client.VncSession;
import com.shinyhut.vernacular.client.exceptions.NoSupportedSecurityTypesException;
import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.protocol.auth.NoSecurityHandler;
import com.shinyhut.vernacular.protocol.auth.SecurityHandler;
import com.shinyhut.vernacular.protocol.auth.VncAuthenticationHandler;
import com.shinyhut.vernacular.protocol.messages.ServerSecurityType;
import com.shinyhut.vernacular.protocol.messages.ServerSecurityTypes;

import java.io.IOException;

import static com.shinyhut.vernacular.protocol.messages.SecurityType.NONE;
import static com.shinyhut.vernacular.protocol.messages.SecurityType.VNC;

public class SecurityTypeNegotiator {

    public SecurityHandler negotiate(VncSession session) throws IOException, VncException {
        SecurityHandler securityHandler;

        if (session.getProtocolVersion().equals(3, 3)) {
            ServerSecurityType serverSecurityType = ServerSecurityType.decode(session.getInputStream());
            switch (serverSecurityType.getSecurityType()) {
                case NONE:
                    securityHandler = new NoSecurityHandler();
                    break;
                case VNC:
                    securityHandler = new VncAuthenticationHandler();
                    break;
                default:
                    throw new NoSupportedSecurityTypesException();
            }
        } else {
            ServerSecurityTypes serverSecurityTypes = ServerSecurityTypes.decode(session.getInputStream());
            if (serverSecurityTypes.getSecurityTypes().contains(NONE)) {
                securityHandler = new NoSecurityHandler();
            } else if (serverSecurityTypes.getSecurityTypes().contains(VNC)) {
                securityHandler = new VncAuthenticationHandler();
            } else {
                throw new NoSupportedSecurityTypesException();
            }
        }

        return securityHandler;
    }

}
