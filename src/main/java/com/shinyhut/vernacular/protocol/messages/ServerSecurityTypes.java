package com.shinyhut.vernacular.protocol.messages;

import com.shinyhut.vernacular.client.exceptions.HandshakingFailedException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.shinyhut.vernacular.protocol.messages.SecurityType.resolve;

public class ServerSecurityTypes {

    private final List<SecurityType> securityTypes;

    private ServerSecurityTypes(List<SecurityType> securityTypes) {
        this.securityTypes = securityTypes;
    }

    public List<SecurityType> getSecurityTypes() {
        return securityTypes;
    }

    public static ServerSecurityTypes decode(InputStream in) throws HandshakingFailedException, IOException {
        DataInputStream dataInput = new DataInputStream(in);
        byte typeCount = dataInput.readByte();

        if (typeCount == 0) {
            ErrorMessage errorMessage = ErrorMessage.decode(in);
            throw new HandshakingFailedException(errorMessage.getMessage());
        }

        List<SecurityType> types = new ArrayList<>();

        for (int i = 0; i < typeCount; i++) {
            byte type = dataInput.readByte();
            resolve(type).ifPresent(types::add);
        }

        return new ServerSecurityTypes(types);
    }
}
