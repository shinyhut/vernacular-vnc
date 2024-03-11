package com.shinyhut.vernacular.protocol.messages;

public enum MessageHeaderFlags {

    TEXT(1),
    RTF(1 << 1),
    HTML(1 << 2),
    DIB(1 << 3),
    FILES(1 << 4),
    CAPS(1 << 24),
    REQUEST(1 << 25),
    PEEK(1 << 26),
    NOTIFY(1 << 27),
    PROVIDE(1 << 28);

    final int code;

    MessageHeaderFlags(int code) {
        this.code = code;
    }
}
