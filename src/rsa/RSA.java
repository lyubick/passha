/**
 * Following package is used to cipher/decipher incoming data.
 *
 * @author lyubick
 */
package rsa;

// TODO: still zeroes is passing as zeroes must to analyze and think something - related only to encrypt Bytes

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Random;

import utilities.Utilities;
import main.Exceptions;
import main.Exceptions.XC;
import logger.Logger;

public final class RSA
{
    private BigInteger       p, q, n, f, e, d;

    /* One byte of information will be coded into 310 bytes of information */
    private static final int RSA_BYTE_ENCRYPTION_LENGTH    = 512;
    private static final int RSA_BYTE_CHUNK_LENGTH         = 128;
    private static final int RSA_BYTE_PADDING_LENGTH       = 11;
    private static final int RSA_BYTE_MESSAGE_BLOCK_LENGTH = RSA_BYTE_CHUNK_LENGTH
                                                                   - RSA_BYTE_PADDING_LENGTH;
    /* Certainty that generated Number is PRIME. (1 - (1/2) ^ Certainty) */
    private static final int RSA_PRIME_CERTAINCY           = 100;

    public String encrypt(String msg)
    {
        return encrypt(msg.getBytes());
    }

    public String encrypt(byte[] message)
    {
        String cipher = "";

        int msgBlockCount =
                (message.length / RSA_BYTE_MESSAGE_BLOCK_LENGTH)
                        + Math.min(message.length % RSA_BYTE_MESSAGE_BLOCK_LENGTH, 1);
        Random r = new Random();

        for (int i = 0; i < msgBlockCount; i++)
        {
            int msgBlockStart = i * RSA_BYTE_MESSAGE_BLOCK_LENGTH;
            byte[] chunk = new byte[RSA_BYTE_CHUNK_LENGTH];
            byte[] padding = new byte[RSA_BYTE_PADDING_LENGTH];

            r.nextBytes(padding);

            int j = 0;

            for (; j < RSA_BYTE_MESSAGE_BLOCK_LENGTH && msgBlockStart + j < message.length; j++)
                chunk[j] = message[msgBlockStart + j];

            for (int k = 0; k < RSA_BYTE_PADDING_LENGTH; j++, k++)
                chunk[j] = padding[k];

            String ASCII = "";
            for (int k = 0; k < j; k++)
            {
                ASCII +=
                        new String("000").substring(Long.toString(Utilities.toLong(chunk[k]))
                                .length()) + Long.toString(Utilities.toLong(chunk[k]));
                Logger.printDebug("b = " + Long.toString(Utilities.toLong(chunk[k])) + "; ASCII "
                        + ASCII);
            }

            String fCipher = new BigInteger(ASCII).modPow(e, n).toString();

            while (fCipher.length() < RSA_BYTE_ENCRYPTION_LENGTH)
                fCipher = "0" + fCipher;

            cipher += fCipher;
        }

        Logger.printDebug(cipher);

        return cipher;
    }

    public byte[] decrypt(String message)
    {
        ByteArrayOutputStream decipher = new ByteArrayOutputStream();
        Logger.printDebug("msg: " + message);

        for (int i = 0; i < message.length(); i += RSA_BYTE_ENCRYPTION_LENGTH)
        {
            String ASCII =
                    new BigInteger(message.substring(i, i + RSA_BYTE_ENCRYPTION_LENGTH)).modPow(d, n)
                            .toString();

            while (ASCII.length() % 3 != 0)
                ASCII = "0" + ASCII;

            Logger.printDebug("ASCII: " + ASCII);

            byte[] chunk = new byte[RSA_BYTE_CHUNK_LENGTH];
            int k = 0;
            for (int j = 0; j < ASCII.length(); j += 3, k++)
                chunk[k] = Utilities.toByte(Integer.parseInt(ASCII.substring(j, j + 3)));

            decipher.write(chunk, 0, k - RSA_BYTE_PADDING_LENGTH);

        }

        return decipher.toByteArray();
    }

    public String decryptToString(String msg)
    {
        String decipher = "";

        byte[] ba = decrypt(msg);

        for (byte b : ba)
            decipher += Character.toString((char) b);

        Logger.printDebug(decipher);

        return decipher;
    }

    private void test() throws Exceptions
    {
        String alphabet =
                "a79d096bef8bb8fbff8f26dc5dadfb7696b6ee487108c0e11a397e22d35262760e3b9f109f9b49c1439716aacd63b7427715d0eecb1023b0ca0a4b3249733e6ca79d096bef8bb8fbff8f26dc5dadfb7696b6ee487108c0e11a397e22d35262760e3b9f109f9b49c1439716aacd63b7427715d0eecb1023b0ca0a4b3249733e6ca79d096bef8bb8fbff8f26dc5dadfb7696b6ee487108c0e11a397e22d35262760e3b9f109f9b49c1439716aacd63b7427715d0eecb1023b0ca0a4b3249733e6c";
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
}
