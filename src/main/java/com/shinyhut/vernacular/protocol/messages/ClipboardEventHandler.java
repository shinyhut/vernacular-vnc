package com.shinyhut.vernacular.protocol.messages;

public interface ClipboardEventHandler {

    void handleClipboardCaps(int flags, int[] lengths);
}
