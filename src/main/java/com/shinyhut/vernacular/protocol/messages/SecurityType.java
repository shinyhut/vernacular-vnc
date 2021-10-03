package com.shinyhut.vernacular.protocol.messages;

import java.util.Optional;

import static java.util.Arrays.stream;

public enum SecurityType {

    NONE(1),
    VNC(2),
    MS_LOGON_2(113);

    private final int code;

    SecurityType(int code) {
        this.code = code;
    }

    public static Optional<SecurityType> resolve(int code) {
        return stream(values()).filter(s -> s.code == code).findFirst();
    }

    public int getCode() {
        return code;
    }
}
