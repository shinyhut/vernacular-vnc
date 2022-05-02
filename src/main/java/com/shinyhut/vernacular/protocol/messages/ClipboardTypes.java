package com.shinyhut.vernacular.protocol.messages;

public interface ClipboardTypes {


    // Formats
    int CLIPBOARD_UTF8 = 1 << 0;
    int CLIPBOARD_RTF = 1 << 1;
    int CLIPBOARD_HTML = 1 << 2;
    int CLIPBOARD_DIB = 1 << 3;
    int CLIPBOARD_FILES = 1 << 4;

    int CLIPBOARD_FORMAT_MASK = 0x0000ffff;

    // Actions
    int CLIPBOARD_CAPS = 1 << 24;
    int CLIPBOARD_REQUEST = 1 << 25;
    int CLIPBOARD_PEEK = 1 << 26;
    int CLIPBOARD_NOTIFY = 1 << 27;
    int CLIPBOARD_PROVIDE = 1 << 28;

    int CLIPBOARD_ACTION_MASK = 0xff000000;


}
