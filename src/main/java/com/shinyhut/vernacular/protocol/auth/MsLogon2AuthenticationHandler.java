package com.shinyhut.vernacular.protocol.auth;

import com.shinyhut.vernacular.client.VncSession;
import com.shinyhut.vernacular.client.exceptions.AuthenticationRequiredException;
import com.shinyhut.vernacular.client.exceptions.UnexpectedVncException;
import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.protocol.messages.SecurityResult;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.util.Random;
import java.util.function.Supplier;

import static com.shinyhut.vernacular.protocol.messages.SecurityType.MS_LOGON_2;
import static com.shinyhut.vernacular.utils.ByteUtils.*;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static javax.crypto.Cipher.ENCRYPT_MODE;

public class MsLogon2AuthenticationHandler implements SecurityHandler {

    private static final long DH_KEY_MAX_BITS = 31;
    private static final long MAX_DH_KEY_VALUE = 1L << DH_KEY_MAX_BITS;

    private final Random random;

    public MsLogon2AuthenticationHandler() throws VncException {
        try {
            this.random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new UnexpectedVncException(e);
        }
    }

    MsLogon2AuthenticationHandler(Random random) {
        this.random = random;
    }

    @Override
    public SecurityResult authenticate(VncSession session) throws VncException, IOException {
        Supplier<String> usernameSupplier = session.getConfig().getUsernameSupplier();
        Supplier<String> passwordSupplier = session.getConfig().getPasswordSupplier();

        if (usernameSupplier == null || passwordSupplier == null) {
            throw new AuthenticationRequiredException();
        }

        InputStream in = session.getInputStream();
        OutputStream out = session.getOutputStream();

        if (!session.getProtocolVersion().equals(3, 3)) {
            requestMsLogon2Authentication(out);
        }

        try {
            byte[] sharedKey = dhKeyExchange(in, out);
            sendEncrypted(out, usernameSupplier.get(), 256, sharedKey);
            sendEncrypted(out, passwordSupplier.get(), 64, sharedKey);
            return SecurityResult.decode(in, session.getProtocolVersion());
        } catch (GeneralSecurityException e) {
            throw new UnexpectedVncException(e);
        }
    }

    private void requestMsLogon2Authentication(OutputStream out) throws IOException {
        out.write(MS_LOGON_2.getCode());
    }

    private byte[] dhKeyExchange(InputStream in, OutputStream out) throws IOException {
        byte[] generatorBytes = new byte[8];
        byte[] modulusBytes = new byte[8];
        byte[] serverPublicKeyBytes = new byte[8];

        DataInput dataInput = new DataInputStream(in);
        dataInput.readFully(generatorBytes);
        dataInput.readFully(modulusBytes);
        dataInput.readFully(serverPublicKeyBytes);

        BigInteger generator = new BigInteger(1, generatorBytes);
        BigInteger modulus = new BigInteger(1, modulusBytes);
        BigInteger serverPublicKey = new BigInteger(1, serverPublicKeyBytes);
        BigInteger clientPrivateKey = BigInteger.valueOf(random.nextLong() % MAX_DH_KEY_VALUE);
        BigInteger clientPublicKey = generator.modPow(clientPrivateKey, modulus);
        BigInteger sharedKey = serverPublicKey.modPow(clientPrivateKey, modulus);

        out.write(padLeft(clientPublicKey.toByteArray(), 8));

        return padLeft(sharedKey.toByteArray(), 8);
    }

    public void sendEncrypted(OutputStream out, String value, int length, byte[] key) throws GeneralSecurityException, IOException {
        byte[] bytes = padRight(value.getBytes(ISO_8859_1), length);
        byte[] encrypted = des(bytes, key);
        out.write(encrypted);
    }

    public byte[] des(byte[] data, byte[] key) throws GeneralSecurityException {
        Cipher des = Cipher.getInstance("DES/CBC/NoPadding");
        IvParameterSpec iv = new IvParameterSpec(key);
        SecretKeySpec keySpec = new SecretKeySpec(reverseBits(key), 0, key.length, "DES");
        des.init(ENCRYPT_MODE, keySpec, iv);
        return des.doFinal(data);
    }
}
