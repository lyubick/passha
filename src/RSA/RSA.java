/**
 * Following package is used to cipher/decipher incoming data.
 *
 * @author lyubick
 */
package RSA;


// TODO: still zeroes is passing as zeroes must to analyze and think something - related only to encrypt Bytes

/* Common */
import Common.Exceptions;
import Common.Exceptions.CODES;
import Common.RC;
import Common.RC.RETURNCODES;
import Common.Utilities;
/* Logging */
import Logger.Logger;

import java.math.BigInteger;



public final class RSA {
    private BigInteger p, q, n, f, e, d;

    /* Constants */
    /* One byte of information will be coded into 40 bytes of information */
    private static final int RSA_BYTE_ENCRYPTION_LENGTH = 40;
    /* Certainty that generated Number is PRIME. (1 - (1/2) ^ Certainty) */
    private static final int RSA_PRIME_CERTAINCY = 100;

    /**
     * @brief function to encrypt incoming message using private key pair (e, n)
     * @category interface
     *
     * @param msg String to encrypt
     * @return encrypted byte array
     */
    public String encrypt(String msg)
    {
        return encrypt(msg.getBytes());
    }

    /**
     * @brief function to encrypt incoming message using private key pair (e, n)
     *
     * @param bytes byte array to encrypt
     * @return encrypted byte array
     */
    public String encrypt(byte[] bytes)
    {
        String cipher  = "";
        String ASCII   = "";
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

    /**
     * @brief function to decrypt incoming message using public key pair (d, n)
     *
     * @param msg
     *            String to decrypt
     * @return decrypted byte array
     */
    public byte[] decryptBytes(String msg)
    {
        byte[] decipher = new byte[msg.length() / RSA_BYTE_ENCRYPTION_LENGTH];
        String ASCII    = "";

        for (int i = 0; i < msg.length(); i += RSA_BYTE_ENCRYPTION_LENGTH)
        {
            ASCII = new BigInteger(msg.substring(i, i + RSA_BYTE_ENCRYPTION_LENGTH)).modPow(d, n).toString();
            decipher[i/RSA_BYTE_ENCRYPTION_LENGTH] = (byte) Long.parseLong(ASCII);
        }

        return decipher;
    }

    /**
     * @brief function to decrypt incoming message using public key pair (d, n)
     *
     * @param msg
     *            String to decrypt
     * @return decrypted String
     */
    public String decrypt(String msg)
    {
        String decipher = "";
        String ASCII    = "";

        for (int i = 0; i < msg.length(); i += RSA_BYTE_ENCRYPTION_LENGTH)
        {
            ASCII = new BigInteger(msg.substring(i, i + RSA_BYTE_ENCRYPTION_LENGTH)).modPow(d, n).toString();
            decipher += Character.toString((char)Long.parseLong(ASCII));
        }

        return decipher;
    }

    /**
     * @brief function to test key pairs (e, n) and (d, n), during test test
     *        message will be encrypted and decrypted. If original message is
     *        equal with encrypted/decrypted test passes and Initialization of
     *        RSA succeed.
     *
     * @return OK/NOK
     */
    private RETURNCODES test()
    {
        String alphabet = "qwertyuiopasdfghjklzxcvbnm1234567890";
        String cipher = "";

        cipher = encrypt(alphabet);
        Logger.printDebug(cipher);

        cipher = decrypt(cipher);
        Logger.printDebug(cipher);

        if (cipher.equals(alphabet))
            return RC.check(RETURNCODES.RC_OK);
        else
            return RC.check(RETURNCODES.RC_NOK);
    }

    /**
     *
     * @return
     */
    private RETURNCODES init()
    {


        while (!p.isProbablePrime(RSA_PRIME_CERTAINCY))
            p = p.add(BigInteger.ONE);

        Logger.printDebug("p: " + p.toString());

        while (!q.isProbablePrime(100))
            q = q.add(BigInteger.ONE);

        Logger.printDebug("q: " + q.toString());

        while (!e.isProbablePrime(100))
            e = e.add(BigInteger.ONE);

        Logger.printDebug("e: " + e.toString());

        n = p.multiply(q);

        Logger.printDebug("n: " + n.toString());

        f = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        Logger.printDebug("f: " + f.toString());

        while (f.gcd(e).intValue() > 1)
            while (!e.isProbablePrime(1))
                e = e.add(BigInteger.ONE);

        d = e.modInverse(f);

        Logger.printDebug("d: " + d.toString());

        return RC.check(test());
    }

    /**
     *
     * @param p
     *            Prime number, got from hash
     * @param q
     *            Prime number, got from hash
     * @param e
     *            Prime number, got from hash
     *
     * @throws Exceptions
     */
    public RSA(String p, String q, String e) throws Exceptions {
        /* Initial values */
        this.p = new BigInteger(p);
        this.q = new BigInteger(q);
        this.n = new BigInteger("0");
        this.f = new BigInteger("0");
        this.e = new BigInteger(e);
        this.d = new BigInteger("0");

        Logger.printDebug("Initial p: " + p.toString());
        Logger.printDebug("Initial q: " + q.toString());
        Logger.printDebug("Initial e: " + e.toString());

        if (RETURNCODES.RC_OK != init())
            throw new Common.Exceptions(CODES.INIT_FAILURE);
    }
}
