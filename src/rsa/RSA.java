package rsa;

// FIXME: finally someone should check situation with zeroes :)

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Random;

import utilities.Utilities;
import main.Exceptions;
import main.Exceptions.XC;

public final class RSA
{
    private BigInteger       p, q, n, f, e, d;

    // Length of encrypted message chunk, used in decipher
    private static final int RSA_BYTE_ENCRYPTED_MESSAGE_LENGTH = 310;
    // RSA chunk length must be equal to Key size in bytes
    // we use SHA-512 results as p, q, e
    private static final int RSA_BYTE_CHUNK_LENGTH             = 32;
    // Minimum recommended padding size according to Standard
    private static final int RSA_BYTE_PADDING_LENGTH           = 11;
    private static final int RSA_BYTE_MESSAGE_BLOCK_LENGTH     = RSA_BYTE_CHUNK_LENGTH - RSA_BYTE_PADDING_LENGTH;
    /* Certainty that generated Number is PRIME. (1 - (1/2) ^ Certainty) */
    private static final int RSA_PRIME_CERTAINCY               = 1000;

    /*
     * Public Main functions
     */

    public String encrypt(byte[] message)
    {
        StringBuilder cipher = new StringBuilder("");
        Random r = new Random();

        int messageBlockCount =
                (message.length / RSA_BYTE_MESSAGE_BLOCK_LENGTH)
                        + Math.min(message.length % RSA_BYTE_MESSAGE_BLOCK_LENGTH, 1);

        for (int i = 0; i < messageBlockCount; i++)
        {
            int messagegBlockStart = i * RSA_BYTE_MESSAGE_BLOCK_LENGTH;
            byte[] chunk = new byte[RSA_BYTE_CHUNK_LENGTH];
            byte[] padding = new byte[RSA_BYTE_PADDING_LENGTH];

            r.nextBytes(padding);

            int chunkLength = 0;

            for (; chunkLength < RSA_BYTE_MESSAGE_BLOCK_LENGTH && messagegBlockStart + chunkLength < message.length; chunkLength++)
                chunk[chunkLength] = message[messagegBlockStart + chunkLength];

            for (int k = 0; k < RSA_BYTE_PADDING_LENGTH; chunkLength++, k++)
                chunk[chunkLength] = padding[k];

            StringBuilder ASCII = new StringBuilder("");
            for (int k = 0; k < chunkLength; k++)
            {
                String tmp = Long.toString(Utilities.toLong(chunk[k]));
                ASCII.append("000".substring(tmp.length()) + tmp);
            }

            StringBuilder fCipher = new StringBuilder(new BigInteger(ASCII.toString()).modPow(e, n).toString());

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
            StringBuilder ASCII =
                    new StringBuilder(new BigInteger(message.substring(i, i + RSA_BYTE_ENCRYPTED_MESSAGE_LENGTH))
                            .modPow(d, n).toString());

            while (ASCII.length() % 3 != 0)
                ASCII.insert(0, "0");

            byte[] chunk = new byte[RSA_BYTE_CHUNK_LENGTH];

            int chunkLength = 0;
            for (int j = 0; j < ASCII.length(); j += 3, chunkLength++)
                chunk[chunkLength] = Utilities.toByte(Integer.parseInt(ASCII.substring(j, j + 3)));

            decipher.write(chunk, 0, chunkLength - RSA_BYTE_PADDING_LENGTH);
        }

        return decipher.toByteArray();
    }

    /*
     * Private main functions
     */

    private void test() throws Exceptions
    {
        // SHA-512 from "Test hash for RSA" x2
        String alphabet =
                "eb6c50e4eff5eed6ed6dd13d76daad743a409da8e65ec22947afd2b0402ed5a7ff75b9dca4760fc5e045f251099ee9b883038e6da0e72d64db3bcb79fd46e39deb6c50e4eff5eed6ed6dd13d76daad743a409da8e65ec22947afd2b0402ed5a7ff75b9dca4760fc5e045f251099ee9b883038e6da0e72d64db3bcb79fd46e39d";
        String cipher = "";

        cipher = encrypt(alphabet);
        cipher = decryptToString(cipher);

        if (!cipher.equals(alphabet)) throw new Exceptions(XC.INIT_FAILURE);
    }

    private void init() throws Exceptions
    {

        while (!p.isProbablePrime(RSA_PRIME_CERTAINCY))
            p = p.add(BigInteger.ONE);

        while (!q.isProbablePrime(RSA_PRIME_CERTAINCY))
            q = q.add(BigInteger.ONE);

        while (!e.isProbablePrime(RSA_PRIME_CERTAINCY))
            e = e.add(BigInteger.ONE);

        n = p.multiply(q);

        f = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        while (f.gcd(e).intValue() > 1)
            while (!e.isProbablePrime(1))
                e = e.add(BigInteger.ONE);

        d = e.modInverse(f);

        test();
    }

    public RSA(String p, String q, String e) throws Exceptions
    {
        /* Initial values */
        this.p = new BigInteger(p, 16);
        this.q = new BigInteger(q, 16);
        this.n = new BigInteger("0");
        this.f = new BigInteger("0");
        this.e = new BigInteger(e, 16);
        this.d = new BigInteger("0");
        init();
    }

    /*
     * Interface methods
     */

    public String encrypt(String msg)
    {
        return encrypt(msg.getBytes());
    }

    public String decryptToString(String msg)
    {
        byte[] ba = decrypt(msg);
        StringBuilder decipher = new StringBuilder(ba.length);

        for (byte b : ba)
            decipher.append(Character.toString((char) b));

        return decipher.toString();
    }
}
