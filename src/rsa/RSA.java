/**
 * Following package is used to cipher/decipher incoming data.
 *
 * @author lyubick
 */
package rsa;

// TODO: still zeroes is passing as zeroes must to analyze and think something - related only to encrypt Bytes

/* Common */
/* Logging */
import java.math.BigInteger;

import main.Exceptions;
import main.Exceptions.XC;
import utilities.Utilities;

public final class RSA
{
    private BigInteger       p, q, n, f, e, d;

    /* One byte of information will be coded into 310 bytes of information */
    private static final int RSA_BYTE_ENCRYPTION_LENGTH = 310;
    /* Certainty that generated Number is PRIME. (1 - (1/2) ^ Certainty) */
    private static final int RSA_PRIME_CERTAINCY        = 100;

    public String encrypt(String msg)
    {
        return encrypt(msg.getBytes());
    }

    public String encrypt(byte[] bytes)
    {
        String cipher = "";
        String ASCII = "";
        String fCipher = "";

        for (int i = 0; i < bytes.length; ++i)
        {
            long tmp = Utilities.toLong(bytes[i]);

            ASCII = Long.toString(tmp);
            fCipher = new BigInteger(ASCII).modPow(e, n).toString();

            while (fCipher.length() < RSA_BYTE_ENCRYPTION_LENGTH)
                fCipher = "0" + fCipher;

            cipher += fCipher.toString();
        }

        return cipher;
    }

    public byte[] decryptBytes(String msg)
    {
        byte[] decipher = new byte[msg.length() / RSA_BYTE_ENCRYPTION_LENGTH];
        String ASCII = "";

        for (int i = 0; i < msg.length(); i += RSA_BYTE_ENCRYPTION_LENGTH)
        {
            ASCII =
                    new BigInteger(msg.substring(i, i + RSA_BYTE_ENCRYPTION_LENGTH)).modPow(d, n)
                            .toString();
            decipher[i / RSA_BYTE_ENCRYPTION_LENGTH] = (byte) Long.parseLong(ASCII);
        }

        return decipher;
    }

    public String decrypt(String msg)
    {
        String decipher = "";
        String ASCII = "";

        for (int i = 0; i < msg.length(); i += RSA_BYTE_ENCRYPTION_LENGTH)
        {
            ASCII =
                    new BigInteger(msg.substring(i, i + RSA_BYTE_ENCRYPTION_LENGTH)).modPow(d, n)
                            .toString();
            decipher += Character.toString((char) Long.parseLong(ASCII));
        }

        return decipher;
    }

    private void test() throws Exceptions
    {
        String alphabet = "qwertyuiopasdfghjklzxcvbnm1234567890";
        String cipher = "";

        cipher = encrypt(alphabet);
        cipher = decrypt(cipher);

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
