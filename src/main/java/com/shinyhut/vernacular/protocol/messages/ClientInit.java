package com.shinyhut.vernacular.protocol.messages;

import java.io.IOException;
import java.io.OutputStream;

public class ClientInit implements Encodable {

    private final boolean shared;

    public ClientInit(boolean shared) {
        this.shared = shared;
    }

    public boolean isShared() {
        return shared;
    }

    @Override
    public void encode(OutputStream out) throws IOException {
        out.write(shared ? new byte[]{1} : new byte[]{0});
    }
}
