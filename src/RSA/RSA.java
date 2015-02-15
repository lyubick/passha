/**
 *
 */
package RSA;

// explicit variable initialization missing
// functions and file missing headers

import java.math.BigInteger;

import Common.Exceptions;
import Common.Exceptions.CODES;
import Common.RC;
import Common.ReturnCodes;
import Logger.Logger;
import Logger.LOGLEVELS;

/**
 * @author lyubick
 *
 */
public final class RSA {
    private BigInteger p, q, n, f, e, d;

    /* Constants */
    private static final int RSA_BYTE_ENCRYPTION_LENGTH = 40;
    private static final int RSA_PRIME_CERTAINCY = 100; /* 1 - (1/2) ^ Certainty */

    /** @brief function to encrypt incoming message using private key pair (e, n)
     *
     * @param [in]msg
     * @return
     */
    public String encrypt(String msg)
    {
        return encrypt(msg.getBytes());
    }

    public String encrypt(byte[] bytes)
    {
        String cipher  = "";
        String ASCII   = "";
        String fCipher = "";

        for (int i = 0; i < bytes.length; ++i)
        {
            long tmp = (long)bytes[i] & 0xFF;

            ASCII = Long.toString(tmp);

            Logger.printDebug(ASCII + " : " + bytes[i]);

            Logger.printDebug("MINUS ili net ? " + new BigInteger(ASCII).toString());

            fCipher = new BigInteger(ASCII).modPow(e, n).toString();

            Logger.printDebug(fCipher);

            while (fCipher.length() < RSA_BYTE_ENCRYPTION_LENGTH)
                fCipher = "0" + fCipher;

            Logger.printDebug(fCipher);

            cipher += fCipher.toString();

        }

        Logger.printDebug("CTO ZA NAHUY: " + cipher);

        return cipher;
    }

    /* TODO there is must be no zeroes */
    public byte[] decryptBytes(String msg)
    {
        byte[] decipher = new byte[msg.length() / RSA_BYTE_ENCRYPTION_LENGTH];
        String ASCII    = "";

        Logger.printDebug(msg);
        Logger.printDebug("" + msg.length());

        for (int i = 0; i < msg.length(); i += RSA_BYTE_ENCRYPTION_LENGTH)
        {

            Logger.printDebug(msg.substring(i, i + RSA_BYTE_ENCRYPTION_LENGTH));

            ASCII = new BigInteger(msg.substring(i, i + RSA_BYTE_ENCRYPTION_LENGTH)).modPow(d, n).toString();

            Logger.printDebug(ASCII);

            decipher[i/RSA_BYTE_ENCRYPTION_LENGTH] = (byte) Long.parseLong(ASCII);

            Logger.printDebug("" + decipher[i/RSA_BYTE_ENCRYPTION_LENGTH]);
        }

        return decipher;
    }

    /**
     *
     * @param msg
     * @return
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
     *
     * @return
     */
    private ReturnCodes test()
    {
        String alphabet = "qwertyuiopasdfghjklzxcvbnm1234567890";
        String cipher = "";

        cipher = encrypt(alphabet);
        Logger.print(LOGLEVELS.DEBUG, cipher);

        cipher = decrypt(cipher);
        Logger.print(LOGLEVELS.DEBUG, cipher);

        if (cipher.equals(alphabet))
            return RC.check(ReturnCodes.RC_OK);
        else
            return RC.check(ReturnCodes.RC_NOK);
    }

    /**
     *
     * @return
     */
    private ReturnCodes init() {


        while (!p.isProbablePrime(RSA_PRIME_CERTAINCY))
            p = p.add(BigInteger.ONE);

        Logger.print(LOGLEVELS.DEBUG, "p: " + p.toString());

        while (!q.isProbablePrime(100))
            q = q.add(BigInteger.ONE);

        Logger.print(LOGLEVELS.DEBUG, "q: " + q.toString());

        while (!e.isProbablePrime(100))
            e = e.add(BigInteger.ONE);

        Logger.print(LOGLEVELS.DEBUG, "e: " + e.toString());

        n = p.multiply(q);

        Logger.print(LOGLEVELS.DEBUG, "n: " + n.toString());

        f = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        Logger.print(LOGLEVELS.DEBUG, "f: " + f.toString());

        while (f.gcd(e).intValue() > 1)
            while (!e.isProbablePrime(1))
                e = e.add(BigInteger.ONE);

        d = e.modInverse(f);

        Logger.print(LOGLEVELS.DEBUG, "d: " + d.toString());

        return RC.check(test());
    }

    /**
     *
     * @param p
     * @param q
     * @param e
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

        Logger.printDebug("p In: " + p.toString());
        Logger.printDebug("q In: " + q.toString());
        Logger.printDebug("e In: " + e.toString());

        if (ReturnCodes.RC_OK != init())
            throw new Common.Exceptions(CODES.INIT_FAILURE);
    }
}
