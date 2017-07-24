package com.shinyhut.vernacular.protocol.auth;

import com.shinyhut.vernacular.client.VncSession;
import com.shinyhut.vernacular.client.exceptions.VncException;

import java.io.IOException;

public interface SecurityHandler {
    void authenticate(VncSession session) throws VncException, IOException;
}
