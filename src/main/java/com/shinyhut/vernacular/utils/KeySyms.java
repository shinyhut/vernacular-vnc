package com.shinyhut.vernacular.utils;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.awt.event.KeyEvent.*;

public class KeySyms {

    private static final Map<Integer, Integer> KEYCODES = new ConcurrentHashMap<>();
    private static final Map<Character, Integer> SYMBOLS = new ConcurrentHashMap<>();

    static {
        KEYCODES.put(VK_BACK_SPACE, 0xff08);
        KEYCODES.put(VK_TAB, 0xff09);
        KEYCODES.put(VK_ENTER, 0xff0d);
        KEYCODES.put(VK_ESCAPE, 0xff1b);
        KEYCODES.put(VK_INSERT, 0xff63);
        KEYCODES.put(VK_DELETE, 0xffff);
        KEYCODES.put(VK_HOME, 0xff50);
        KEYCODES.put(VK_END, 0xff57);
        KEYCODES.put(VK_PAGE_UP, 0xff55);
        KEYCODES.put(VK_PAGE_DOWN, 0xff56);
        KEYCODES.put(VK_LEFT, 0xff51);
        KEYCODES.put(VK_UP, 0xff52);
        KEYCODES.put(VK_RIGHT, 0xff53);
        KEYCODES.put(VK_DOWN, 0xff54);
        KEYCODES.put(VK_F1, 0xffbe);
        KEYCODES.put(VK_F2, 0xffbf);
        KEYCODES.put(VK_F3, 0xffc0);
        KEYCODES.put(VK_F4, 0xffc1);
        KEYCODES.put(VK_F5, 0xffc2);
        KEYCODES.put(VK_F6, 0xffc3);
        KEYCODES.put(VK_F7, 0xffc4);
        KEYCODES.put(VK_F8, 0xffc5);
        KEYCODES.put(VK_F9, 0xffc6);
        KEYCODES.put(VK_F10, 0xffc7);
        KEYCODES.put(VK_F11, 0xffc8);
        KEYCODES.put(VK_F12, 0xffc9);
        KEYCODES.put(VK_SHIFT, 0xffe1);
        KEYCODES.put(VK_CONTROL, 0xffe3);
        KEYCODES.put(VK_META, 0xffe7);
        KEYCODES.put(VK_ALT, 0xffe9);
    }

    static {
        SYMBOLS.put((char) 0x00, 0x0040);
        SYMBOLS.put((char) 0x01, 0x0061);
        SYMBOLS.put((char) 0x02, 0x0062);
        SYMBOLS.put((char) 0x03, 0x0063);
        SYMBOLS.put((char) 0x04, 0x0064);
        SYMBOLS.put((char) 0x05, 0x0065);
        SYMBOLS.put((char) 0x06, 0x0066);
        SYMBOLS.put((char) 0x07, 0x0067);
        SYMBOLS.put((char) 0x08, 0x0068);
        SYMBOLS.put((char) 0x09, 0x0069);
        SYMBOLS.put((char) 0x0a, 0x006a);
        SYMBOLS.put((char) 0x0b, 0x006b);
        SYMBOLS.put((char) 0x0c, 0x006c);
        SYMBOLS.put((char) 0x0d, 0x006d);
        SYMBOLS.put((char) 0x0e, 0x006e);
        SYMBOLS.put((char) 0x0f, 0x006f);
        SYMBOLS.put((char) 0x10, 0x0070);
        SYMBOLS.put((char) 0x11, 0x0071);
        SYMBOLS.put((char) 0x12, 0x0072);
        SYMBOLS.put((char) 0x13, 0x0073);
        SYMBOLS.put((char) 0x14, 0x0074);
        SYMBOLS.put((char) 0x15, 0x0075);
        SYMBOLS.put((char) 0x16, 0x0076);
        SYMBOLS.put((char) 0x17, 0x0077);
        SYMBOLS.put((char) 0x18, 0x0078);
        SYMBOLS.put((char) 0x19, 0x0079);
        SYMBOLS.put((char) 0x1a, 0x007a);
        SYMBOLS.put((char) 0x1b, 0x005b);
        SYMBOLS.put((char) 0x1c, 0x005c);
        SYMBOLS.put((char) 0x1d, 0x005d);
        SYMBOLS.put((char) 0x1e, 0x005e);
        SYMBOLS.put((char) 0x1f, 0x005f);
    }

    public static Optional<Integer> keySym(int keyCode, char symbol) {
        if (KEYCODES.containsKey(keyCode)) {
            return Optional.of(KEYCODES.get(keyCode));
        }
        if (SYMBOLS.containsKey(symbol)) {
            return Optional.of(SYMBOLS.get(symbol));
        }
        if (symbol != CHAR_UNDEFINED) {
            return Optional.of((int) symbol);
        }
        return Optional.empty();
    }
}
