package com.shinyhut.vernacular.protocol.messages;

import com.shinyhut.vernacular.client.exceptions.AuthenticationFailedException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SecurityResult {

    private final boolean success;
    private final String errorMessage;

    private SecurityResult(boolean success) {
        this(success, null);
    }

    private SecurityResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static SecurityResult decode(InputStream in) throws AuthenticationFailedException, IOException {
        DataInputStream dataInput = new DataInputStream(in);
        int resultCode = dataInput.readInt();
        SecurityResult result;
        if (resultCode == 1) {
            ErrorMessage errorMessage = ErrorMessage.decode(in);
            result = new SecurityResult(false, errorMessage.getMessage());
        } else {
            result = new SecurityResult(true);
        }
        return result;
    }

}
