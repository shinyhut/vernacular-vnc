package com.shinyhut.vernacular.protocol.messages;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.zip.DeflaterInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import static com.shinyhut.vernacular.protocol.messages.ClipboardTypes.*;

public class ServerCutText {

    private static final int MAX_CUT_TEXT = 256 * 1024;

    private final String text;
    private String error;

    public ServerCutText(String text) {
        this.text = text;
    }

    public ServerCutText(String text, String error) {
        this.text = text;
        this.error = error;
    }

    public String getText() {
        return text;
    }

    public static ServerCutText decode(InputStream in) throws IOException {
        return decode(in, null);
    }

    public static ServerCutText decode(InputStream in, ClipboardEventHandler handler) throws IOException {
        DataInputStream dataInput = new DataInputStream(in);
        dataInput.readFully(new byte[4]);
        int textLength = dataInput.readInt();
        if (textLength < 0) {
            return readExtendedClipboard(dataInput, -textLength, handler);
        }
        byte[] textBytes = new byte[textLength];
        dataInput.readFully(textBytes);
        String text = new String(textBytes, Charset.forName("ISO-8859-1"));
        return new ServerCutText(text);
    }

    /**
     * Very non-standard non-documented RFB extension.
     * This code is largerly based on UltraVNC/TigerVNC "Extended Clipboard" support
     * (@see https://github.com/TigerVNC/tigervnc/pull/834/files)
     *
     *
     * @param in
     * @param textLength
     * @param handler
     * @return
     * @throws IOException
     */
    private static ServerCutText readExtendedClipboard(DataInputStream in, int textLength, ClipboardEventHandler handler) throws IOException {
        //First 4 bytes are always message flags.
        if (textLength < 4) {
            throw new IOException("Invalid clipboard message length");
        }

        if (textLength > MAX_CUT_TEXT) {
            //Log error / warning
            in.skip(textLength);
            return new ServerCutText("", "Cut text longer than allowed maximum " + MAX_CUT_TEXT + " bytes");
        }


        int flags = in.readInt();
        int action = flags & CLIPBOARD_ACTION_MASK;

        if ((action & CLIPBOARD_CAPS) > 0) {

            int[] lengths = new int[16];
            int num = 0;

            for (int i = 0; i < 16; ++i) {
                if ((flags & (1 << i)) > 0) {
                    ++num;
                }
            }
            if (textLength < (4 + 4 * num)) {
                throw new IOException("CLIPBOARD_CAPS Invalid extended clipboard message");
            }
            num = 0;
            for (int i = 0; i < 16; ++i) {
                if ((flags & (1 << i)) > 0) {
                    lengths[num++] = in.readInt();
                }
            }
            if(handler != null) {
                handler.handleClipboardCaps(flags, lengths);
            }
        } else if(action == CLIPBOARD_PROVIDE) {
            InflaterInputStream iif = new InflaterInputStream(in);
            int compressedTextLength = ByteBuffer.wrap(iif.readNBytes(4)).getInt();
            byte[] text = iif.readNBytes(compressedTextLength);
            return new ServerCutText(new String(text));
        }
        return new ServerCutText("");
    }
}
