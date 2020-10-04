package org.kgbt.passha.core.rsa;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Random;

import org.kgbt.passha.core.common.Utilities;
import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.Exceptions.XC;

public final class RSA
{
    public enum RSA_SIZE {
        _2048,
        _4096,
    }

    private BigInteger       p, q, n, f, e, d;

    // Length of encrypted message chunk, used in decipher
    private int RSA_BYTE_ENCRYPTED_MESSAGE_LENGTH = 310;
    // Message representative, an integer between 0 and n � 1 (p and q are 64
    // byte long Hex)
    private static final int RSA_BYTE_CHUNK_LENGTH             = 64 - 1;
    // Minimum recommended padding size according to Standard
    private static final int RSA_BYTE_PADDING_LENGTH           = 11;
    private static final int RSA_BYTE_MESSAGE_BLOCK_LENGTH     = RSA_BYTE_CHUNK_LENGTH - RSA_BYTE_PADDING_LENGTH;
    /* Certainty that generated Number is PRIME. (1 - (1/2) ^ Certainty) */
    private static final int RSA_PRIME_CERTAINCY               = 1000;

    public String encrypt(byte[] message)
    {
        StringBuilder cipher = new StringBuilder();
        Random r = new Random();

        int messageBlockCount = (message.length / RSA_BYTE_MESSAGE_BLOCK_LENGTH)
            + Math.min(message.length % RSA_BYTE_MESSAGE_BLOCK_LENGTH, 1);

        for (int i = 0; i < messageBlockCount; i++)
        {
            int messagegBlockStart = i * RSA_BYTE_MESSAGE_BLOCK_LENGTH;

            int blockLength = Math.min(RSA_BYTE_MESSAGE_BLOCK_LENGTH, message.length - messagegBlockStart);
            int chunkLength = RSA_BYTE_PADDING_LENGTH + blockLength;

            byte[] chunk = new byte[chunkLength];
            byte[] padding = new byte[RSA_BYTE_PADDING_LENGTH];

            r.nextBytes(padding);
            while (padding[0] == 0x00 || (padding[0] & 0x80) != 0)
            {
                // be sure BigInteger will be positive
                padding[0] = Utilities.toByte(r.nextInt());
            }

            System.arraycopy(padding, 0, chunk, 0, RSA_BYTE_PADDING_LENGTH);

            System.arraycopy(message, messagegBlockStart, chunk, RSA_BYTE_PADDING_LENGTH, blockLength);

            StringBuilder fCipher = new StringBuilder(new BigInteger(chunk).modPow(e, n).toString());

            while (fCipher.length() < RSA_BYTE_ENCRYPTED_MESSAGE_LENGTH)
                fCipher.insert(0, "0");

            cipher.append(fCipher);
        }

        return cipher.toString();
    }

    public byte[] decrypt(String message)
    {
        ByteArrayOutputStream decipher = new ByteArrayOutputStream();

        for (int i = 0; i < message.length(); i += RSA_BYTE_ENCRYPTED_MESSAGE_LENGTH)
        {
            BigInteger fDecipher =
                new BigInteger(message.substring(i, i + RSA_BYTE_ENCRYPTED_MESSAGE_LENGTH)).modPow(d, n);

            byte[] chunk = fDecipher.toByteArray();

            decipher.write(chunk, RSA_BYTE_PADDING_LENGTH, chunk.length - RSA_BYTE_PADDING_LENGTH);
        }

        return decipher.toByteArray();
    }

    private void test() throws Exceptions
    {
        // SHA-512 from "Test hash for RSA" x2
        String alphabet =
            "4dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db94dff4ea340f0a823f15d3f4f01ab62eae0e5da579ccb851f8db9dfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510adfe84c58b2b37b89903a740e1ee172da793a6e79d560e5f7f9bd058a12a280433ed6fa46510a";
        String cipher = encrypt(alphabet);
        cipher = decryptToString(cipher);

        if (!cipher.equals(alphabet)) throw new Exceptions(XC.INIT_FAILURE);
    }

    private void init() throws Exceptions
    {
        final BigInteger TWO = BigInteger.ONE.add(BigInteger.ONE);

        if (p.mod(TWO).equals(BigInteger.ZERO))
            p = p.add(BigInteger.ONE);

        if (q.mod(TWO).equals(BigInteger.ZERO))
            q = q.add(BigInteger.ONE);

        if (e.mod(TWO).equals(BigInteger.ZERO))
            e = e.add(BigInteger.ONE);

        while (!p.isProbablePrime(RSA_PRIME_CERTAINCY))
            p = p.add(TWO);

        while (!q.isProbablePrime(RSA_PRIME_CERTAINCY))
            q = q.add(TWO);

        while (!e.isProbablePrime(RSA_PRIME_CERTAINCY))
            e = e.add(TWO);

        n = p.multiply(q);

        f = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        while (f.gcd(e).intValue() > 1)
            while (!e.isProbablePrime(1))
                e = e.add(BigInteger.ONE);

        d = e.modInverse(f);

        test();
    }

    public RSA(String p, String q, String e, RSA_SIZE size) throws Exceptions
    {
        if (size == RSA_SIZE._4096)
            RSA_BYTE_ENCRYPTED_MESSAGE_LENGTH = 1300;

        /* Initial values */
        this.p = new BigInteger(p, 16);
        this.q = new BigInteger(q, 16);
        this.n = new BigInteger("0");
        this.f = new BigInteger("0");
        this.e = new BigInteger(e, 16);
        this.d = new BigInteger("0");
        init();
    }

    public String encrypt(String msg)
    {
        return encrypt(msg.getBytes());
    }

    public String decryptToString(String msg)
    {
        byte[] ba = decrypt(msg);
        StringBuilder decipher = new StringBuilder(ba.length);

        for (byte b : ba)
            decipher.append((char) b);

        return decipher.toString();
    }
}
