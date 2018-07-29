package com.shinyhut.vernacular.protocol.messages;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SetColorMapEntries {

    private final int firstColor;
    private final List<ColorMapEntry> colors;

    public SetColorMapEntries(int firstColor, List<ColorMapEntry> colors) {
        this.firstColor = firstColor;
        this.colors = colors;
    }

    public int getFirstColor() {
        return firstColor;
    }

    public List<ColorMapEntry> getColors() {
        return colors;
    }

    public static SetColorMapEntries decode(InputStream in) throws IOException {
        DataInputStream dataInput = new DataInputStream(in);
        dataInput.readFully(new byte[2]);
        int firstColor = dataInput.readUnsignedShort();
        int numberOfColors = dataInput.readUnsignedShort();
        List<ColorMapEntry> colors = new ArrayList<>();
        for (int i = 0; i < numberOfColors; i++) {
            colors.add(ColorMapEntry.decode(in));
        }
        return new SetColorMapEntries(firstColor, colors);
    }
}
