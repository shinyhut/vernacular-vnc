package com.shinyhut.vernacular.protocol.messages;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ServerQemuAudio {

	private final byte[] audio;

	public ServerQemuAudio(byte[] audio) {
		this.audio = audio;
	}

	public byte[] getAudio() {
		return audio;
	}

	public static ServerQemuAudio decode(InputStream in) throws IOException {
		DataInputStream dataInput = new DataInputStream(in);
		dataInput.readFully(new byte[2]);
		int operation = dataInput.readUnsignedShort();
		if (operation == 2) {
			int length = dataInput.readInt();
			byte[] out = new byte[length];
			dataInput.readFully(out);
			return new ServerQemuAudio(out);
		}
		return new ServerQemuAudio(new byte[0]);
	}
}