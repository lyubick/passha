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
   // init them to null, maybe?
    private BigInteger p, q, n, f, e, d;

    private String authorization = "";

    private static final int RSA_BYTE_ENCRYPTION_LENGTH = 40;

    public String getAuthorizationStatus()
    {
        return this.authorization;
    }

    public String encrypt(String msg)
    {
        String cipher  = "";
        String ASCII   = "";
        String fCipher = "";

        for (int i = 0; i < msg.length(); ++i)
        {
            ASCII = Integer.toString(msg.codePointAt(i));
            fCipher = new BigInteger(ASCII).modPow(e, n).toString();

            while (fCipher.length() < RSA_BYTE_ENCRYPTION_LENGTH)
                fCipher = "0" + fCipher;

            cipher += fCipher.toString();
        }

        return cipher;
    }

    public String decrypt(String msg) {
        String decipher = "";
        String ASCII    = "";

        for (int i = 0; i < msg.length(); i += RSA_BYTE_ENCRYPTION_LENGTH)
        {
            ASCII = new BigInteger(msg.substring(i, i + RSA_BYTE_ENCRYPTION_LENGTH)).modPow(d, n).toString();
            decipher += Character.toString((char)Long.parseLong(ASCII));
        }

        return decipher;
    }

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

    private ReturnCodes init() {


         // maybe don't use hardcode, no?
        while (!p.isProbablePrime(100))
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

    public RSA(String p, String q, String e) throws Exceptions {
        this.p = new BigInteger(p);
        this.q = new BigInteger(q);
        this.n = new BigInteger("0");
        this.f = new BigInteger("0");
        this.e = new BigInteger(e);
        this.d = new BigInteger("0");

        Logger.print(LOGLEVELS.DEBUG, "pin: " + p.toString());
        Logger.print(LOGLEVELS.DEBUG, "qin: " + q.toString());
        Logger.print(LOGLEVELS.DEBUG, "ein: " + e.toString());

        // it's better to write call first, then constant
        // it's more reliable to accept RC_OK as succes, and else fail, rather than RC_NOK fail and all else - success
        if (ReturnCodes.RC_NOK == init())
        {
           // no need for this variable, since if failed - no object is eligible for grabage collection
            authorization = "FAIL";
            throw new Common.Exceptions(CODES.INIT_FAILURE);
        }
        else
        {
            authorization = "PASS";
        }
    }
}
