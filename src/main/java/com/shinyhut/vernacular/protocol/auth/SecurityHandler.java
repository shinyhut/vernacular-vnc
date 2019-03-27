package com.shinyhut.vernacular.protocol.auth;

import com.shinyhut.vernacular.client.VncSession;
import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.protocol.messages.SecurityResult;

import java.io.IOException;

public interface SecurityHandler {
    SecurityResult authenticate(VncSession session) throws VncException, IOException;
}
