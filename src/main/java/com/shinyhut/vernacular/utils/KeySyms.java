package com.shinyhut.vernacular.utils;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.awt.event.KeyEvent.*;

public class KeySyms {

    private static final Map<Integer, Integer> OVERRIDES = new ConcurrentHashMap<>();

    static {
        OVERRIDES.put(VK_BACK_SPACE, 0xff08);
        OVERRIDES.put(VK_TAB, 0xff09);
        OVERRIDES.put(VK_ENTER, 0xff0d);
        OVERRIDES.put(VK_ESCAPE, 0xff1b);
        OVERRIDES.put(VK_INSERT, 0xff63);
        OVERRIDES.put(VK_DELETE, 0xffff);
        OVERRIDES.put(VK_HOME, 0xff50);
        OVERRIDES.put(VK_END, 0xff57);
        OVERRIDES.put(VK_PAGE_UP, 0xff55);
        OVERRIDES.put(VK_PAGE_DOWN, 0xff56);
        OVERRIDES.put(VK_LEFT, 0xff51);
        OVERRIDES.put(VK_UP, 0xff52);
        OVERRIDES.put(VK_RIGHT, 0xff53);
        OVERRIDES.put(VK_DOWN, 0xff54);
        OVERRIDES.put(VK_F1, 0xffbe);
        OVERRIDES.put(VK_F2, 0xffbf);
        OVERRIDES.put(VK_F3, 0xffc0);
        OVERRIDES.put(VK_F4, 0xffc1);
        OVERRIDES.put(VK_F5, 0xffc2);
        OVERRIDES.put(VK_F6, 0xffc3);
        OVERRIDES.put(VK_F7, 0xffc4);
        OVERRIDES.put(VK_F8, 0xffc5);
        OVERRIDES.put(VK_F9, 0xffc6);
        OVERRIDES.put(VK_F10, 0xffc7);
        OVERRIDES.put(VK_F11, 0xffc8);
        OVERRIDES.put(VK_F12, 0xffc9);
        OVERRIDES.put(VK_SHIFT, 0xffe1);
        OVERRIDES.put(VK_CONTROL, 0xffe3);
        OVERRIDES.put(VK_META, 0xffe7);
        OVERRIDES.put(VK_ALT, 0xffe9);
    }

    public static Optional<Integer> keySym(int keyCode, char symbol) {
        if (OVERRIDES.containsKey(keyCode)) {
            return Optional.of(OVERRIDES.get(keyCode));
        }
        if (symbol != CHAR_UNDEFINED) {
            return Optional.of((int) symbol);
        }
        return Optional.empty();
    }
}
